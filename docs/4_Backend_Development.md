### 1. **프로젝트 설정**

Spring Boot 프로젝트를 설정하고 필요한 종속성을 추가합니다.

#### A. **Spring Initializr를 사용한 프로젝트 생성**

- **기본 설정**:
  - Project: Maven Project
  - Language: Java
  - Spring Boot Version: 2.7.x (또는 최신 LTS 버전)
  - Packaging: Jar
  - Java Version: 11 또는 17 (LTS 버전 권장)
- **필요한 Dependencies**:
  - Spring Web
  - Spring Data JPA
  - Spring Security (로그인/회원가입 기능을 위해)
  - Oracle Driver
  - Lombok (코드 간결화를 위해)
  - Spring Boot DevTools (개발 편의성을 위해)
  - Validation (입력 유효성 검사를 위해)
  - Thymeleaf (선택적, 서버사이드 템플릿 엔진 사용 시)

#### B. **`application.properties` 설정**

Oracle 데이터베이스와 연결을 위해 `src/main/resources/application.properties` 또는 `application.yml`에 다음 설정을 추가합니다:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 2. **엔티티 및 리포지토리 설계**

데이터베이스 설계를 바탕으로 JPA 엔티티와 리포지토리를 정의합니다.

#### A. **엔티티 클래스 예제**

1. **User 엔티티**

```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    
    private String email;
    
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 기타 필요한 필드와 메서드
}
```

2. **Post 엔티티**

```java
@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 기타 필요한 필드와 메서드
}
```

3. **Comment 엔티티**

```java
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;  // 답글(1단계 깊이만 허용)

    private String content;

    private LocalDateTime createdAt;

    // 기타 필요한 필드와 메서드
}
```

4. **File 엔티티**

```java
@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    
    private String originalName;
    
    private String filePath;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    @Column(name = "entity_type")
    private String entityType;  // 'POST' 또는 'COMMENT'

    @Column(name = "entity_id")
    private Long entityId;  // 관련 엔티티 ID
}
```

#### B. **리포지토리 인터페이스**

각 엔티티에 대한 JPA 리포지토리 인터페이스를 생성합니다.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
}

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
```

### 3. **서비스 구현**

서비스 레이어에서 비즈니스 로직을 구현합니다.

```java
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(Long userId, String title, String content) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }
    
    // 기타 CRUD 메서드 구현
}
```

### 4. **컨트롤러 구현**

컨트롤러 레이어에서 API 요청을 처리합니다.

```java
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getPosts();
    }

    @PostMapping
    public Post createPost(@RequestParam Long userId, @RequestParam String title, @RequestParam String content) {
        return postService.createPost(userId, title, content);
    }

    // 기타 API 엔드포인트 구현
}
```

### 5. **파일 업로드 관리**

파일 업로드는 `MultipartFile`을 사용하여 처리할 수 있습니다.

#### A. **파일 서비스 구현**

```java
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public void uploadFiles(String entityType, Long entityId, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = "uploads/" + uniqueFileName;
            
            // 파일 저장 로직
            try {
                File dest = new File(filePath);
                file.transferTo(dest);
                
                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(uniqueFileName);
                fileEntity.setOriginalName(file.getOriginalFilename());
                fileEntity.setFilePath(filePath);
                fileEntity.setFileSize(file.getSize());
                fileEntity.setUploadedAt(LocalDateTime.now());
                fileEntity.setEntityType(entityType);
                fileEntity.setEntityId(entityId);
                
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
    }
}
```

#### B. **파일 컨트롤러 구현**

```java
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(
        @RequestParam("entityType") String entityType,
        @RequestParam("entityId") Long entityId,
        @RequestParam("files") List<MultipartFile> files) {

        fileService.uploadFiles(entityType, entityId, files);
        return ResponseEntity.ok("Files uploaded successfully.");
    }

    // 기타 파일 다운로드 및 삭제 엔드포인트 구현
}
```
