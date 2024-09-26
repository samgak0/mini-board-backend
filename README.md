# 게시판 프로젝트 (Bulletin Board Project)

## 개요

이 프로젝트는 Spring Boot와 React를 사용하여 개발된 게시판 애플리케이션입니다. 사용자들은 게시글을 작성, 수정, 삭제할 수 있으며, 댓글을 달고 이미지 파일을 업로드할 수 있습니다. 백엔드는 Spring Boot와 Oracle DB로 구축되었고, 프론트엔드는 React로 구축되었습니다.

## 주요 기능

- **사용자 관리**: 회원가입, 로그인, 로그아웃, 사용자 프로필 관리.
- **게시글 관리**: 게시글 작성, 수정, 삭제, 목록 및 상세 보기.
- **댓글 관리**: 게시글에 대한 댓글 작성, 수정, 삭제, 1단계 깊이의 답글 기능.
- **파일 업로드**: 게시글과 댓글에 다수의 이미지 파일 업로드 가능.
- **좋아요 기능**: 게시글과 댓글에 대한 좋아요 기능.
- **통합 검색**: 제목, 내용, 작성자별 검색 기능.
- **관리자 기능**: 모든 게시글과 댓글을 관리할 수 있는 관리자 계정 기능.

## 기술 스택

- **백엔드**: Java, Spring Boot, Spring Data JPA, Oracle DB
- **프론트엔드**: JavaScript, React, React Router, Axios
- **빌드 도구**: Maven (백엔드), npm (프론트엔드)
- **웹 서버**: Nginx
- **CI/CD**: GitHub Actions

## 설치 및 실행

### 1. 백엔드(Spring Boot)

#### A. 요구사항

- Java 11 이상
- Maven 3.6 이상
- Oracle Database 12c 이상

#### B. 설치 및 실행

```bash
# 저장소 클론
git clone https://github.com/your-repo/my-board-app.git
cd my-board-app/backend

# Maven 의존성 설치 및 빌드
./mvnw clean install

# 애플리케이션 실행
./mvnw spring-boot:run
```

- 애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 2. 프론트엔드(React)

#### A. 요구사항

- Node.js 16 이상
- npm 7 이상

#### B. 설치 및 실행

```bash
# 프론트엔드 디렉토리로 이동
cd my-board-app/frontend

# npm 패키지 설치
npm install

# 애플리케이션 실행
npm start
```

- 애플리케이션은 기본적으로 `http://localhost:3000`에서 실행됩니다.

## API 문서

백엔드 API 문서는 Swagger를 통해 제공됩니다. 애플리케이션을 실행한 후 `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

## 테스트

### 1. 백엔드 테스트

JUnit 및 Mockito를 사용하여 백엔드 테스트를 수행할 수 있습니다.

```bash
# 테스트 실행
./mvnw test
```

### 2. 프론트엔드 테스트

React Testing Library와 Jest를 사용하여 프론트엔드 테스트를 수행할 수 있습니다.

```bash
# 테스트 실행
npm test
```

## 배포

이 프로젝트는 GitHub Actions를 통해 CI/CD 파이프라인이 설정되어 있으며, 코드를 변경할 때마다 자동으로 빌드, 테스트 및 배포가 이루어집니다.

### 배포 스크립트

서버에 배포하기 위한 스크립트는 `deploy-backend.sh`와 `deploy-frontend.sh` 파일로 제공됩니다.

```bash
# 백엔드 배포
bash /path/to/deploy-backend.sh

# 프론트엔드 배포
bash /path/to/deploy-frontend.sh
```

### 서버 설정

- **웹 서버**: Nginx가 정적 파일 서빙 및 리버스 프록시로 설정됩니다.
- **SSL 인증서**: Let's Encrypt를 사용하여 SSL 인증서를 적용합니다.

## 환경 변수 설정

프로젝트에서 사용하는 환경 변수는 `.env` 파일을 통해 설정합니다.

```plaintext
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## docker Commend

```bash
docker run --name redis -d -p 6379:6379 redis:7.4.0
docker run --name oracle-xe -d -p 1521:1521 -p 5500:5500 container-registry.oracle.com/database/express:21.3.0-xe
```

## 기여 가이드

1. 이슈를 등록하고 논의해주세요.
2. 기능 개발 또는 버그 수정을 위한 브랜치를 생성합니다.
3. 브랜치에서 작업 후, PR(Pull Request)을 생성합니다.
4. 코드 리뷰를 통해 코드의 품질을 향상시킵니다.

## 문의

궁금한 점이 있거나 문제가 발생했다면 [이슈](https://github.com/your-repo/my-board-app/issues)에 등록하거나 이메일로 연락주세요: your.email@example.com.
