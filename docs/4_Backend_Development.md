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

4. **PostFile 엔티티**

```java
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_files")
@Getter
@Setter
@NoArgsConstructor
public class PostFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // 게시글과의 관계

    private String fileName;       // 저장된 파일 이름
    private String originalName;   // 원래 파일 이름
    private String filePath;       // 파일이 저장된 경로
    private Long fileSize;         // 파일 크기
    private LocalDateTime createdAt; // 업로드 일자

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // 생성 시 현재 시간 설정
    }
}
```


5. **CommentFile 엔티티**

```java
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment_files")
@Getter
@Setter
@NoArgsConstructor
public class CommentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;  // 댓글과의 관계

    private String fileName;       // 저장된 파일 이름
    private String originalName;   // 원래 파일 이름
    private String filePath;       // 파일이 저장된 경로
    private Long fileSize;         // 파일 크기
    private LocalDateTime createdAt; // 업로드 일자

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // 생성 시 현재 시간 설정
    }
}
```

6. ### **Like 엔티티**

```java
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private LocalDateTime createdAt;

    // 추가적인 메서드나 필드를 필요에 따라 정의할 수 있습니다.
}
```

### 설명

- **`@Entity`**: 이 클래스가 JPA 엔티티임을 나타냅니다.
- **`@Table(name = "likes")`**: 이 엔티티가 `likes` 테이블과 매핑된다는 것을 정의합니다.
- **`@Id`**와 **`@GeneratedValue`**: 기본 키를 정의하고 자동으로 값을 생성합니다.
- **`@ManyToOne`**: 각 좋아요가 특정 사용자, 게시물, 댓글에 연결되어 있음을 나타내며, Lazy Loading을 사용합니다.
- **`@JoinColumn`**: 각 관계에서 외래 키를 매핑합니다.

이 엔티티를 사용하면 좋아요 기능을 구현할 수 있으며, 필요한 경우 추가적인 필드나 메서드를 정의하여 기능을 확장할 수 있습니다.

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

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByPost(Post post);
    long countByComment(Comment comment);
}

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPost(Post post);
}

public interface CommentFileRepository extends JpaRepository<CommentFile, Long> {
    List<CommentFile> findByComment(Comment comment);
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
## 로그인 설정

React에서 로그인 요청을 `POST` 방식으로 보내는 경우, RESTful한 접근 방식으로 서버 측에서 세션을 관리하는 방법을 설명하겠습니다. RESTful API를 설계하면서 세션 기반 인증을 구현하는 방법은 다음과 같습니다:

### 1. **백엔드(Spring Framework) 설정**

#### A. **Spring Security 설정**

- **RESTful 로그인 엔드포인트**: 로그인 요청을 처리할 엔드포인트를 설정합니다. 로그인 성공 시 세션을 생성하고, 클라이언트에 세션 ID를 포함한 쿠키를 설정합니다.

  ```java
  @RestController
  @RequestMapping("/api")
  public class AuthController {

      @Autowired
      private AuthenticationManager authenticationManager;

      @PostMapping("/login")
      public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                  loginRequest.getEmail(), loginRequest.getPassword());
          Authentication authentication = authenticationManager.authenticate(authenticationToken);

          // 세션 생성
          HttpSession session = request.getSession(true);
          session.setAttribute("user", authentication.getPrincipal());

          return ResponseEntity.ok().build();
      }

      // 로그아웃 처리
      @PostMapping("/logout")
      public ResponseEntity<?> logout(HttpServletRequest request) {
          HttpSession session = request.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          return ResponseEntity.ok().build();
      }
  }
  ```

- **Spring Security 설정**: `WebSecurityConfigurerAdapter`를 통해 세션을 관리하고, CSRF 보호를 비활성화할 수 있습니다. RESTful API에서는 CSRF 보호가 불필요할 수 있습니다.

  ```java
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {

      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
              .csrf().disable() // CSRF 비활성화
              .authorizeRequests()
                  .antMatchers("/api/login", "/api/logout").permitAll()
                  .anyRequest().authenticated()
                  .and()
              .sessionManagement()
                  .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
      }
  }
  ```

### 2. **프론트엔드(React) 설정**

#### A. **로그인 요청 보내기**

- React에서 로그인 요청을 `POST` 방식으로 서버에 보냅니다. 서버는 로그인 성공 시 쿠키를 통해 세션 ID를 클라이언트에 설정합니다.

  ```javascript
  const handleLogin = async () => {
    try {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include', // 쿠키를 포함하여 요청
        body: JSON.stringify({
          email: email,
          password: password
        })
      });

      if (response.ok) {
        // 로그인 성공, 세션 ID가 쿠키에 저장됨
        // 클라이언트 상태를 업데이트하거나 리다이렉트
      } else {
        // 로그인 실패 처리
      }
    } catch (error) {
      console.error('로그인 오류:', error);
    }
  };
  ```

#### B. **인증된 사용자만 접근 가능한 기능 구현**

- 인증된 사용자만 접근할 수 있는 페이지나 컴포넌트를 보호합니다. 서버 측 세션을 활용하여 인증된 사용자만 접근하도록 합니다.

  ```javascript
  import { Route, Redirect } from 'react-router-dom';

  const PrivateRoute = ({ component: Component, ...rest }) => (
    <Route
      {...rest}
      render={props =>
        // 서버에서 세션을 확인하기 위해 API 요청을 사용
        fetch('/api/check-session', { credentials: 'include' })
          .then(response => response.ok ? <Component {...props} /> : <Redirect to="/login" />)
          .catch(() => <Redirect to="/login" />)
      }
    />
  );
  ```

### 3. **세션 확인 API**

- 세션 확인을 위한 API를 추가하여 클라이언트가 로그인 상태를 확인할 수 있도록 합니다.

  ```java
  @RestController
  @RequestMapping("/api")
  public class SessionController {

      @GetMapping("/check-session")
      public ResponseEntity<?> checkSession(HttpSession session) {
          if (session.getAttribute("user") != null) {
              return ResponseEntity.ok().build();
          }
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
  }
  ```

### 4. **결론**

세션 기반 인증을 RESTful하게 구현하는 것은 가능합니다. 로그인 요청을 처리하고 세션을 관리하며, 클라이언트에서 세션 기반 인증을 확인하여 사용자가 로그인된 상태에서만 특정 기능에 접근할 수 있도록 설정할 수 있습니다. 이 방식은 RESTful API와 세션 관리를 통합하여 보안성을 유지하면서도 사용자 인증 상태를 관리할 수 있습니다.
다음은 API 호출 명령어와 각 API의 형식 및 설명을 포함한 문서입니다.

---

# MiniBoard API Documentation

## Table of Contents
1. [API Authentication](#api-authentication)
2. [User Registration](#user-registration)
3. [Check Username Availability](#check-username-availability)
4. [Check Email Availability](#check-email-availability)
5. [Login](#login)
6. [Get Posts](#get-posts)

---

### API Authentication

모든 인증된 요청은 `Session Cookie`를 통해 이루어집니다. 

성공적인 로그인 후, `-c cookies.txt` 옵션을 사용하여 세션 쿠키를 저장하고, 이후 요청에 `-b cookies.txt`로 쿠키를 전달하여 인증을 유지합니다.

---

### User Registration

#### API Endpoint
- **URL**: `/api/users/register`
- **Method**: POST

#### Request Parameters
- **username** (string, required)
- **email** (string, required)
- **password** (string, required)

#### cURL Command

- **Success Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/register -d "username=newUser&email=newuser@example.com&password=password123" -b "cookie.txt"
  ```

- **Failure (Email Already Used) Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/register -d "username=newUser&email=existing@example.com&password=password123"
  ```

---

### Check Username Availability

#### API Endpoint
- **URL**: `/api/users/check/username`
- **Method**: POST

#### Request Parameters
- **username** (string, required)

#### cURL Command

- **Success Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/check/username -d "username=newUser" -c "cookie.txt"
  ```

- **Failure (Username Missing) Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/check/username
  ```

---

### Check Email Availability

#### API Endpoint
- **URL**: `/api/users/check/email`
- **Method**: POST

#### Request Parameters
- **email** (string, required)

#### cURL Command

- **Success Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/check/email -d "email=newuser@example.com" -b "cookie.txt"
  ```

- **Failure (Invalid Email Format) Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/check/email -d "email=invalid-email"  -b "cookie.txt"
  ```

---

### Login

#### API Endpoint
- **URL**: `/api/users/login`
- **Method**: POST

#### Request Parameters
- **username** (string, required)
- **password** (string, required)

#### cURL Command

- **Success Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/login -d "username=user&password=password" -c cookies.txt
  ```

- **Failure (Wrong Password) Example**
  ```bash
  curl -X POST http://localhost:8080/api/users/login  -d "username=user&password=wrongpassword" -b "cookie.txt"
  ```

---

### Get Posts

#### API Endpoint
- **URL**: `/api/posts`
- **Method**: GET
- **Authentication**: Required

#### cURL Command

- **Success Example (Authenticated)**
  ```bash
  curl -X GET http://localhost:8080/api/posts -b cookies.txt
  ```

- **Failure (Unauthenticated)**
  ```bash
  curl -X GET http://localhost:8080/api/posts
  ```
