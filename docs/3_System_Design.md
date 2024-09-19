### 1. **아키텍처 설계**

**3계층 아키텍처**로 구성됩니다:

- **프론트엔드 (React)**
  - 사용자 인터페이스(UI)와 사용자 경험(UX)을 담당.
  - REST API를 통해 백엔드와 통신.
- **백엔드 (Spring Boot)**
  - 비즈니스 로직 처리 및 데이터베이스와의 통신을 담당.
  - REST API를 제공하여 프론트엔드와 데이터베이스를 연결.
- **데이터베이스 (Oracle)**
  - 데이터를 영구적으로 저장하고 관리.

### 2. **데이터베이스 설계**

#### A. **사용자 테이블 (users)**

- **id**: NUMBER (PK) - 사용자 고유 식별자
- **username**: VARCHAR2(50) - 사용자 이름
- **email**: VARCHAR2(100) - 이메일 주소
- **password**: VARCHAR2(255) - 암호화된 비밀번호
- **created_at**: TIMESTAMP - 계정 생성 일자
- **updated_at**: TIMESTAMP - 계정 정보 수정 일자

#### B. **게시글 테이블 (posts)**

- **id**: NUMBER (PK) - 게시글 고유 식별자
- **user_id**: NUMBER (FK) - 작성자 (users 테이블의 id)
- **title**: VARCHAR2(255) - 게시글 제목
- **content**: CLOB - 게시글 내용
- **created_at**: TIMESTAMP - 게시글 작성 일자
- **updated_at**: TIMESTAMP - 게시글 수정 일자

#### C. **댓글 테이블 (comments)**

- **id**: NUMBER (PK) - 댓글 고유 식별자
- **post_id**: NUMBER (FK) - 해당 댓글의 게시글 ID (posts 테이블의 id)
- **user_id**: NUMBER (FK) - 작성자 (users 테이블의 id)
- **content**: VARCHAR2(1000) - 댓글 내용
- **created_at**: TIMESTAMP - 댓글 작성 일자
- **parent_comment_id**: NUMBER (FK) - 부모 댓글 ID (comments 테이블의 id, 1단계 깊이의 답글만 허용)

#### D. **좋아요 테이블 (likes)**

- **id**: NUMBER (PK) - 좋아요 고유 식별자
- **user_id**: NUMBER (FK) - 사용자 ID (users 테이블의 id)
- **post_id**: NUMBER (FK) - 게시글 ID (posts 테이블의 id)
- **comment_id**: NUMBER (FK) - 댓글 ID (comments 테이블의 id)

#### E. **파일 테이블 (files)**

게시글과 댓글에 업로드된 파일을 관리하는 테이블입니다.

- **id**: NUMBER (PK) - 파일 고유 식별자
- **file_name**: VARCHAR2(255) - 실제 저장된 파일 이름(유니크한 랜덤 이름)
- **original_name**: VARCHAR2(255) - 사용자가 업로드한 원래 파일 이름
- **file_path**: VARCHAR2(255) - 파일이 저장된 폴더 경로
- **file_size**: NUMBER - 파일 크기 (KB, MB 단위)
- **uploaded_at**: TIMESTAMP - 파일 업로드 일자
- **entity_type**: VARCHAR2(20) - 파일이 속한 엔티티 유형 ('POST' 또는 'COMMENT')
- **entity_id**: NUMBER - 파일이 속한 엔티티의 ID (게시글 ID 또는 댓글 ID)

### 3. **API 설계**

#### A. **사용자 API**

- **POST /api/users/register**: 회원가입
- **POST /api/users/login**: 로그인
- **POST /api/users/logout**: 로그아웃
- **PUT /api/users/{id}/password**: 비밀번호 변경

#### B. **게시글 API**

- **GET /api/posts**: 게시글 목록 조회
- **GET /api/posts/{id}**: 게시글 상세 조회
- **POST /api/posts**: 게시글 작성
- **PUT /api/posts/{id}**: 게시글 수정
- **DELETE /api/posts/{id}**: 게시글 삭제

#### C. **댓글 API**

- **GET /api/posts/{postId}/comments**: 특정 게시글의 댓글 목록 조회
- **POST /api/posts/{postId}/comments**: 댓글 작성
- **PUT /api/comments/{id}**: 댓글 수정
- **DELETE /api/comments/{id}**: 댓글 삭제

#### D. **좋아요 API**

- **POST /api/posts/{postId}/like**: 게시글 좋아요
- **POST /api/comments/{commentId}/like**: 댓글 좋아요

#### E. **파일 API**

- **POST /api/files/upload?entityType={entityType}&entityId={entityId}**: 특정 엔티티에 다수의 파일 업로드.
- **GET /api/files/{fileId}**: 파일 다운로드.
- **DELETE /api/files/{fileId}**: 파일 삭제.

### 4. **프론트엔드 구조 설계**

#### A. **컴포넌트 구조**

- **App.js**: 메인 애플리케이션 컴포넌트.
- **Pages/**
  - **HomePage.js**: 게시글 목록 페이지.
  - **PostDetailPage.js**: 게시글 상세 페이지.
  - **UserProfilePage.js**: 사용자 프로필 페이지.
- **Components/**
  - **PostList.js**: 게시글 목록 컴포넌트.
  - **PostForm.js**: 게시글 작성/수정 컴포넌트.
  - **CommentList.js**: 댓글 목록 컴포넌트.
  - **CommentForm.js**: 댓글 작성/수정 컴포넌트.
  - **LikeButton.js**: 좋아요 버튼 컴포넌트.
  - **FileUpload.js**: 파일 업로드 컴포넌트 (다중 파일 업로드 지원).
  - **FileList.js**: 업로드된 파일 목록을 표시하고 삭제 기능을 제공하는 컴포넌트.

#### B. **상태 관리**

- **Redux** 또는 **Context API**를 사용하여 전역 상태 관리.
- 사용자의 로그인 상태, 게시글 목록, 댓글 목록, 파일 업로드 상태 등을 전역 상태로 관리.

Oracle SQL을 사용하여 제시된 데이터베이스 설계를 테이블로 변형해보겠습니다. Oracle SQL에서는 데이터 타입이 다소 다를 수 있으므로 적절하게 변환하였습니다. 각 테이블의 `CREATE TABLE` 문을 아래와 같이 작성할 수 있습니다.

아래는 각 테이블과 칼럼에 대한 주석을 약간 더 길게 작성한 버전입니다.

### 5. **DB Schema**

#### A. **Users Table (users)**

```sql
CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Table for storing user information';
COMMENT ON COLUMN users.id IS 'Unique identifier for each user';
COMMENT ON COLUMN users.username IS 'User login name for authentication';
COMMENT ON COLUMN users.email IS 'Email address for communication and recovery';
COMMENT ON COLUMN users.password IS 'Encrypted password for user authentication';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user information was last updated';
```

#### B. **Posts Table (posts)**

```sql
CREATE TABLE posts (
    id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    title VARCHAR2(255) NOT NULL,
    content CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE posts IS 'Table for storing post information';
COMMENT ON COLUMN posts.id IS 'Unique identifier for each post';
COMMENT ON COLUMN posts.user_id IS 'ID of the user who created the post';
COMMENT ON COLUMN posts.title IS 'Title of the post for display';
COMMENT ON COLUMN posts.content IS 'Content of the post in text format';
COMMENT ON COLUMN posts.created_at IS 'Timestamp when the post was created';
COMMENT ON COLUMN posts.updated_at IS 'Timestamp when the post information was last updated';
```

#### C. **Comments Table (comments)**

```sql
CREATE TABLE comments (
    id NUMBER PRIMARY KEY,
    post_id NUMBER REFERENCES posts(id),
    user_id NUMBER REFERENCES users(id),
    content VARCHAR2(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id NUMBER REFERENCES comments(id)
);

COMMENT ON TABLE comments IS 'Table for storing comment information';
COMMENT ON COLUMN comments.id IS 'Unique identifier for each comment';
COMMENT ON COLUMN comments.post_id IS 'ID of the post to which the comment belongs';
COMMENT ON COLUMN comments.user_id IS 'ID of the user who wrote the comment';
COMMENT ON COLUMN comments.content IS 'Text content of the comment';
COMMENT ON COLUMN comments.created_at IS 'Timestamp when the comment was created';
COMMENT ON COLUMN comments.parent_comment_id IS 'ID of the parent comment for nested replies';
```

#### D. **Likes Table (likes)**

```sql
CREATE TABLE likes (
    id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    post_id NUMBER REFERENCES posts(id),
    comment_id NUMBER REFERENCES comments(id)
);

COMMENT ON TABLE likes IS 'Table for storing like information';
COMMENT ON COLUMN likes.id IS 'Unique identifier for each like action';
COMMENT ON COLUMN likes.user_id IS 'ID of the user who liked the content';
COMMENT ON COLUMN likes.post_id IS 'ID of the post that received a like';
COMMENT ON COLUMN likes.comment_id IS 'ID of the comment that received a like';
```

#### E. **Files Table (files)**

```sql
CREATE TABLE files (
    id NUMBER PRIMARY KEY,
    file_name VARCHAR2(255) NOT NULL,
    original_name VARCHAR2(255) NOT NULL,
    file_path VARCHAR2(255) NOT NULL,
    file_size NUMBER NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    entity_type VARCHAR2(20) CHECK (entity_type IN ('POST', 'COMMENT')),
    entity_id NUMBER
);

COMMENT ON TABLE files IS 'Table for storing file information';
COMMENT ON COLUMN files.id IS 'Unique identifier for each file';
COMMENT ON COLUMN files.file_name IS 'Name of the file as stored in the system';
COMMENT ON COLUMN files.original_name IS 'Original name of the file as uploaded by the user';
COMMENT ON COLUMN files.file_path IS 'File path indicating where the file is stored';
COMMENT ON COLUMN files.file_size IS 'Size of the file in bytes';
COMMENT ON COLUMN files.uploaded_at IS 'Timestamp when the file was uploaded';
COMMENT ON COLUMN files.entity_type IS 'Type of entity associated with the file (e.g., POST or COMMENT)';
COMMENT ON COLUMN files.entity_id IS 'ID of the entity that the file is linked to';
```
