### 1. **서버 준비**

먼저, 배포할 서버를 준비하고 필요한 환경을 설정합니다.

#### A. **서버 요구사항**

1. **운영체제**: 대부분의 경우 Ubuntu 또는 CentOS와 같은 Linux 배포판을 사용합니다.
2. **JDK 설치**: Spring Boot 애플리케이션을 실행하기 위해 JDK 11 또는 17(LTS)을 설치해야 합니다.
3. **Node.js 설치**: 프론트엔드(React) 애플리케이션 빌드 및 실행을 위해 Node.js가 필요합니다.
4. **웹 서버**: Nginx나 Apache를 사용하여 정적 파일 서빙 및 리버스 프록시 설정을 합니다.
5. **데이터베이스**: Oracle DB가 이미 설치되고 설정되어 있어야 합니다.

#### B. **필수 패키지 설치**

서버에 필요한 패키지를 설치합니다.

```bash
# Java 설치
sudo apt update
sudo apt install openjdk-11-jdk

# Node.js 설치 (NodeSource 사용)
curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt install -y nodejs

# Nginx 설치
sudo apt install nginx
```

#### C. **서버 보안 및 네트워크 설정**

1. **SSH 보안 설정**: 비밀번호 인증을 비활성화하고 SSH 키 인증을 사용하여 보안을 강화합니다.
2. **방화벽 설정**: UFW(Uncomplicated Firewall)를 사용하여 HTTP(80), HTTPS(443), SSH(22) 포트만 열어둡니다.

```bash
sudo ufw allow 'Nginx Full'
sudo ufw allow OpenSSH
sudo ufw enable
```

3. **도메인 및 SSL 인증서 설정**: Let's Encrypt를 사용하여 무료 SSL 인증서를 발급하고 Nginx에 적용합니다.

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d example.com -d www.example.com
```

### 2. **배포 스크립트 작성**

CI/CD 파이프라인에서 사용할 배포 스크립트를 작성하여 서버에서 자동으로 애플리케이션을 배포할 수 있도록 합니다.

#### A. **배포 디렉터리 설정**

서버에 배포할 디렉터리를 생성하고 필요한 파일을 복사하거나 이동합니다.

```bash
# 서버에서 실행
mkdir -p /var/www/my-backend
mkdir -p /var/www/my-frontend
```

#### B. **Spring Boot 백엔드 배포 스크립트**

`deploy-backend.sh` 스크립트를 작성하여 백엔드 애플리케이션을 빌드하고 실행합니다.

```bash
#!/bin/bash

# 환경 변수 설정
REPO_DIR=/var/www/my-backend
JAR_NAME=my-backend-app.jar
BACKUP_DIR=/var/www/my-backend/backup

# 백업 디렉터리 생성
mkdir -p $BACKUP_DIR

# 백엔드 코드 업데이트
cd $REPO_DIR
git pull origin main

# 기존 실행 중인 애플리케이션 종료
pkill -f $JAR_NAME

# 빌드 및 백업
./mvnw clean package -DskipTests
cp $REPO_DIR/target/$JAR_NAME $BACKUP_DIR/$(date +%F_%T)_$JAR_NAME

# 애플리케이션 실행
nohup java -jar target/$JAR_NAME > /dev/null 2>&1 &
```

- `git pull`: 최신 코드를 가져옵니다.
- `pkill -f $JAR_NAME`: 실행 중인 백엔드 프로세스를 종료합니다.
- `mvn clean package`: Maven으로 백엔드 애플리케이션을 빌드합니다.
- `nohup java -jar target/$JAR_NAME`: 애플리케이션을 백그라운드에서 실행합니다.

#### C. **React 프론트엔드 배포 스크립트**

`deploy-frontend.sh` 스크립트를 작성하여 프론트엔드 애플리케이션을 빌드하고 배포합니다.

```bash
#!/bin/bash

# 환경 변수 설정
REPO_DIR=/var/www/my-frontend
BUILD_DIR=$REPO_DIR/build
NGINX_DIR=/var/www/html

# 프론트엔드 코드 업데이트
cd $REPO_DIR
git pull origin main

# 프론트엔드 빌드
npm install
npm run build

# 빌드된 파일을 Nginx 디렉터리로 이동
rm -rf $NGINX_DIR/*
cp -r $BUILD_DIR/* $NGINX_DIR/
```

- `npm install`: 필요한 의존성을 설치합니다.
- `npm run build`: React 애플리케이션을 프로덕션용으로 빌드합니다.
- `cp -r $BUILD_DIR/* $NGINX_DIR/`: Nginx 디렉터리로 빌드된 파일을 복사합니다.

### 3. **GitHub Actions에서 SSH를 사용한 배포 설정**

#### A. **GitHub Secrets 설정**

GitHub Actions에서 SSH를 통해 서버에 액세스하려면 GitHub Secrets에 다음과 같은 보안 정보를 설정해야 합니다:

- **SERVER_HOST**: 배포할 서버의 IP 주소 또는 도메인.
- **SERVER_USER**: SSH 사용자 이름.
- **SERVER_SSH_KEY**: SSH 프라이빗 키. GitHub Secrets에 저장하기 전에 SSH 키를 생성하고 퍼블릭 키를 서버에 추가합니다.

#### B. **GitHub Actions에 SSH 배포 설정 추가**

`ci-cd.yml` 파일에서 **Deploy to Server** 단계에 SSH 명령을 추가하여 앞서 작성한 스크립트를 실행합니다.

```yaml
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
          # 백엔드 배포
          ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} 'bash /var/www/my-backend/deploy-backend.sh'
          
          # 프론트엔드 배포
          ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} 'bash /var/www/my-frontend/deploy-frontend.sh'
```

- `ssh` 명령을 사용하여 원격 서버에서 배포 스크립트를 실행합니다.
- 배포가 완료되면 백엔드와 프론트엔드가 모두 최신 상태로 배포됩니다.

### 4. **Nginx 리버스 프록시 및 정적 파일 서빙 설정**

1. **Nginx 설정 파일 수정**

   `/etc/nginx/sites-available/default` 파일을 수정하여 리버스 프록시와 정적 파일 서빙을 설정합니다.

```nginx
server {
    listen 80;
    server_name example.com www.example.com;

    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

2. **Nginx 설정 테스트 및 재시작**

```bash
sudo nginx -t  # 설정 파일 테스트
sudo systemctl restart nginx  # Nginx 재시작
```

### 5. **배포 모니터링 및 관리**

- **로그 파일 확인**: `journalctl`, `tail -f /var/log/nginx/access.log` 및 `error.log`를 사용하여 애플리케이션과 Nginx의 로그를 확인합니다.
- **자동화된 롤백**: 문제가 발생할 경우 수동 또는 자동으로 롤백할 수 있는 방법을 마련합니다.

### 요약

이 가이드를 통해 CI/CD 파이프라인에서 자동 배포를 설정하고, GitHub Actions를 활용하여 백엔드와 프론트엔드 애플리케이션을 서버에 배포하는 방법을 안내했습니다. 또한, 서버 설정, 보안 강화, Nginx 설정 등 배포 환경의 구성을 포함했습니다. 추가적으로 도움이 필요하거나 더 알고 싶은 부분이 있다면 알려주세요!
