### 1. **통합 준비**

#### A. **백엔드와 프론트엔드 연동 설정**

1. **백엔드 서버 설정**
   - Spring Boot 백엔드 서버가 `http://localhost:8080`에서 실행되고 있는지 확인합니다.
   - 필요한 경우 CORS 설정을 통해 프론트엔드 서버의 요청을 허용합니다. `WebMvcConfigurer`를 사용하여 CORS 설정을 추가할 수 있습니다.

   ```java
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/**")
                   .allowedOrigins("http://localhost:3000") // 프론트엔드 서버 URL
                   .allowedMethods("GET", "POST", "PUT", "DELETE")
                   .allowedHeaders("*");
       }
   }
   ```

2. **프론트엔드 서버 설정**
   - React 개발 서버가 `http://localhost:3000`에서 실행되고 있는지 확인합니다.
   - `package.json`에 프록시 설정을 추가하여 백엔드 API에 대한 요청이 프론트엔드에서 제대로 라우팅되도록 설정합니다.

   ```json
   "proxy": "http://localhost:8080"
   ```

#### B. **공통 환경 설정 파일 준비**

백엔드와 프론트엔드의 공통 환경 설정을 위해 `.env` 파일을 사용하여 환경 변수를 관리할 수 있습니다. React 프로젝트의 루트에 `.env` 파일을 추가합니다:

```bash
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

`src/services/api.js`에서 환경 변수를 참조하도록 수정합니다:

```javascript
const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
```

### 2. **통합 테스트 (Integration Testing)**

통합 테스트는 백엔드와 프론트엔드가 제대로 연동되어 데이터가 흐르고, 전체 시스템이 예상대로 작동하는지 확인하는 과정입니다.

#### A. **기본 흐름 통합 테스트 시나리오**

1. **사용자 회원가입 및 로그인 테스트**
   - 프론트엔드에서 회원가입 페이지로 이동하여 새로운 사용자를 등록.
   - 백엔드의 `/api/users/register` 엔드포인트를 호출하고, DB에 새로운 사용자가 저장되는지 확인.
   - 로그인 페이지로 이동하여 새로 생성한 사용자로 로그인하고, 세션 또는 토큰이 올바르게 생성되는지 확인.

2. **게시글 작성, 수정, 삭제 테스트**
   - 로그인 상태에서 게시글 작성 페이지로 이동.
   - 새로운 게시글을 작성하고, 백엔드의 `/api/posts` 엔드포인트가 호출되는지 확인.
   - 작성된 게시글의 상세 페이지로 이동하여 수정 버튼 클릭 후, 게시글을 수정하고 저장.
   - 게시글이 수정되었는지 확인하고, 수정된 게시글의 내용이 DB에 반영되었는지 확인.
   - 게시글 삭제 버튼을 클릭하여 게시글을 삭제하고, 백엔드에서 게시글이 삭제되었는지 확인.

3. **댓글 작성 및 삭제 테스트**
   - 특정 게시글의 상세 페이지로 이동.
   - 댓글을 작성하고, 백엔드의 `/api/posts/{postId}/comments` 엔드포인트가 호출되는지 확인.
   - 댓글이 목록에 제대로 표시되는지 확인하고, 댓글 삭제 후 정상적으로 삭제되는지 확인.

4. **파일 업로드 및 다운로드 테스트**
   - 게시글 작성 또는 댓글 작성 시 파일 업로드 기능을 테스트.
   - 파일이 서버에 업로드되고, DB에 파일 메타데이터가 저장되는지 확인.
   - 업로드된 파일을 다운로드하여 정상적으로 동작하는지 확인.

#### B. **통합 테스트 도구**

1. **Postman** 또는 **Insomnia**
   - API 요청을 시뮬레이션하고 백엔드가 올바르게 작동하는지 테스트할 수 있습니다.
   - 요청/응답을 검사하고, 필요한 경우 유효성 검사를 추가할 수 있습니다.

2. **React Testing Library** 및 **Jest**
   - 프론트엔드 컴포넌트 및 화면 단위 테스트를 수행할 수 있습니다.
   - API 호출이 올바르게 수행되고 컴포넌트가 예상대로 동작하는지 확인할 수 있습니다.

### 3. **단위 테스트 (Unit Testing)**

단위 테스트는 개별 컴포넌트나 함수가 올바르게 작동하는지 확인하는 과정입니다.

#### A. **백엔드 단위 테스트 (Spring Boot)**

1. **JUnit 및 Mockito**를 사용하여 서비스 레이어와 리포지토리의 단위 테스트를 작성합니다.

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @Test
    public void testCreatePost() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setUser(user);

        Mockito.when(postRepository.save(any(Post.class))).thenReturn(post);

        Post createdPost = postService.createPost(user.getId(), "Test Title", "Test Content");
        assertEquals("Test Title", createdPost.getTitle());
        assertEquals("Test Content", createdPost.getContent());
        assertEquals(user.getId(), createdPost.getUser().getId());
    }
}
```

2. **기타 테스트 케이스 작성**
   - 댓글 작성/수정/삭제에 대한 테스트.
   - 파일 업로드/다운로드 서비스에 대한 테스트.
   - 사용자 인증/인가 로직에 대한 테스트.

#### B. **프론트엔드 단위 테스트 (React)**

1. **Jest와 React Testing Library**를 사용하여 컴포넌트 테스트를 작성합니다.

```javascript
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import HomePage from '../pages/HomePage';

test('게시글 목록이 렌더링되는지 테스트', async () => {
  render(<HomePage />);
  
  const postListItems = await screen.findAllByRole('listitem');
  expect(postListItems.length).toBeGreaterThan(0);  // 최소 하나 이상의 게시글이 표시되는지 확인
});
```

2. **API 호출 모킹 (Mocking)**
   - **Axios Mock Adapter**를 사용하여 API 호출을 모킹하고 컴포넌트가 올바르게 동작하는지 테스트합니다.

```javascript
import MockAdapter from 'axios-mock-adapter';
import api from '../services/api';

// API 모킹 설정
const mock = new MockAdapter(api);

mock.onGet('/posts').reply(200, [
  { id: 1, title: 'Test Post 1', content: 'Test Content 1' },
  { id: 2, title: 'Test Post 2', content: 'Test Content 2' },
]);
```

### 4. **테스트 자동화 및 CI/CD 설정**

1. **CI/CD 파이프라인 설정**
   - **GitHub Actions**, **Jenkins**, **GitLab CI** 등을 사용하여 테스트 자동화를 설정하고, 코드가 푸시될 때마다 자동으로 테스트를 실행합니다.
   - 빌드 및 배포 과정도 함께 설정하여, 코드의 품질을 보장합니다.

2. **테스트 보고서 생성**
   - 테스트 결과를 시각적으로 확인할 수 있도록 테스트 보고서를 생성합니다.
   - **JUnit Report**나 **Allure Report**와 같은 도구를 사용하여 테스트 결과를 시각화합니다.

### 1. **CI/CD 파이프라인 개요**

CI/CD 파이프라인은 다음과 같은 주요 단계로 구성됩니다:

1. **Continuous Integration (CI)**:
   - **코드 빌드(Build)**: 코드가 변경될 때마다 자동으로 빌드하고 종속성을 설치합니다.
   - **테스트(Test)**: 빌드된 코드에 대해 단위 테스트와 통합 테스트를 실행하여 오류를 조기에 발견합니다.

2. **Continuous Deployment (CD)**:
   - **배포(Deploy)**: 모든 테스트를 통과하면 애플리케이션을 스테이징(staging) 또는 프로덕션 환경에 자동으로 배포합니다.

### 2. **GitHub Actions 설정**

GitHub Actions를 사용하여 CI/CD 파이프라인을 설정하기 위해 `.github/workflows` 디렉터리에 YAML 형식의 워크플로 파일을 추가합니다.

#### A. **프로젝트 루트에 GitHub Actions 설정 파일 생성**

1. **CI/CD를 위한 디렉토리 및 파일 생성**

```bash
mkdir -p .github/workflows
touch .github/workflows/ci-cd.yml
```

2. **`.github/workflows/ci-cd.yml` 파일에 워크플로 정의 추가**

아래의 YAML 예제는 **Java Spring Boot 백엔드**와 **React 프론트엔드** 모두에 대한 CI/CD 파이프라인을 설정하는 데 필요한 내용을 포함합니다.

```yaml
name: CI/CD Pipeline

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  backend:
    name: Build and Test Backend
    runs-on: ubuntu-latest

    services:
      oracle:
        image: store/oracle/database-enterprise:12.2.0.1-slim
        ports:
          - 1521:1521
        options: >-
          --health-cmd="echo 'SELECT 1 FROM DUAL;' | sqlplus -s system/oracle@localhost:1521/ORCLPDB1" 
          --health-interval=10s
          --health-timeout=5s
          --health-retries=3

    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'

      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build with Maven
        run: mvn clean install -DskipTests=true

      - name: Run tests
        run: mvn test

  frontend:
    name: Build and Test Frontend
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'

      - name: Cache Node.js modules
        uses: actions/cache@v2
        with:
          path: ~/.npm
          key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
          restore-keys: |
            ${{ runner.os }}-node-

      - name: Install dependencies
        run: npm install

      - name: Run tests
        run: npm test

      - name: Build for production
        run: npm run build

  deploy:
    name: Deploy to Server
    runs-on: ubuntu-latest
    needs: [backend, frontend]

    steps:
      - name: Deploy to Server via SSH
        uses: appleboy/ssh-action@v0.1.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /path/to/backend
            git pull origin main
            ./mvnw spring-boot:stop
            ./mvnw spring-boot:start
            cd /path/to/frontend
            git pull origin main
            npm install
            npm run build
```

### 3. **CI/CD 구성 설명**

#### A. **워크플로 트리거**

- `on: push`: `main` 브랜치로의 **push** 이벤트 또는 **pull request** 이벤트가 발생할 때마다 워크플로가 실행됩니다.

#### B. **백엔드 빌드 및 테스트**

1. **백엔드 작업(job) 설정**:
   - **환경**: `ubuntu-latest`로 설정하고, Oracle Database 도커 이미지 컨테이너를 사용합니다.
   - **Java 설정**: `actions/setup-java@v2`를 사용하여 JDK 11을 설정합니다.
   - **Maven 캐시**: `actions/cache@v2`를 사용하여 Maven 의존성을 캐시합니다.
   - **빌드 및 테스트**: `mvn clean install -DskipTests=true`로 빌드하고 `mvn test`로 테스트를 실행합니다.

2. **Oracle Database 서비스**:
   - Oracle DB를 도커 컨테이너로 실행하여 테스트 환경에서 사용할 수 있도록 설정합니다.

#### C. **프론트엔드 빌드 및 테스트**

1. **프론트엔드 작업(job) 설정**:
   - **환경**: `ubuntu-latest`로 설정합니다.
   - **Node.js 설정**: `actions/setup-node@v2`를 사용하여 Node.js 버전 16을 설치합니다.
   - **Node.js 모듈 캐시**: `actions/cache@v2`를 사용하여 NPM 모듈을 캐시합니다.
   - **빌드 및 테스트**: `npm install`로 종속성을 설치하고, `npm test`로 테스트를 실행한 후 `npm run build`로 프로덕션 빌드를 생성합니다.

#### D. **배포 단계**

- **배포 작업(job) 설정**:
  - **SSH Action 사용**: `appleboy/ssh-action@v0.1.3`을 사용하여 SSH를 통해 서버에 접근하고, 명령어를 실행하여 애플리케이션을 배포합니다.
  - **배포 명령어**: Git에서 최신 코드를 풀(Pull)한 다음, 백엔드 및 프론트엔드를 재시작합니다.

#### E. **보안 설정**

- **GitHub Secrets 사용**:
  - `secrets.SERVER_HOST`, `secrets.SERVER_USER`, `secrets.SERVER_SSH_KEY`는 GitHub Secrets에 저장된 보안 정보입니다.
  - 이러한 정보는 GitHub 저장소 설정에서 `Secrets` 탭을 통해 설정할 수 있습니다.

### 4. **테스트 및 배포 자동화 설정**

#### A. **테스트 자동화**

- **CI 파이프라인**이 코드를 변경할 때마다 자동으로 테스트를 실행하고, 실패 시 알림을 통해 문제를 조기에 발견할 수 있습니다.

#### B. **배포 자동화**

- **CD 파이프라인**이 모든 테스트를 통과한 후 자동으로 애플리케이션을 서버에 배포하여 최신 버전의 애플리케이션이 항상 동작하도록 합니다.

### 5. **추가 고려사항**

- **다양한 환경에 대한 배포**: 스테이징, QA, 프로덕션 등 여러 환경에 대해 다른 워크플로 파일을 설정하거나 조건을 추가할 수 있습니다.
- **모니터링 및 로깅**: 애플리케이션이 배포된 후 모니터링 도구(예: Prometheus, Grafana)와 로깅 도구(예: ELK Stack)를 설정하여 시스템의 상태를 모니터링합니다.
- **알림 설정**: GitHub Actions의 실패나 성공 시 Slack, 이메일 등으로 알림을 받을 수 있도록 설정할 수 있습니다.
