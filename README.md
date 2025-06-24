# 뜨개 API

뜨개질 서비스를 제공하는 Spring Boot 애플리케이션입니다.
각종 기능 추가 예정입니다.

## 기술 스택

- 백엔드 : Java 21, Spring Boot 3.5.0, JPA, QueryDSL, Gradle, MySQL 8

- 인프라: AWS EC2 (Ubuntu 22.04), AWS RDS(예정), S3 GitHub, Docker

- 기타: Slack
## 주요 기능

- 추가예정

### MySQL 실행

MySQL 서버는 Docker를 통해 실행

```bash
# MySQL 컨테이너 실행
docker run -d \
  --name knit_db \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=knit_db \
  -p 3307:3306 \
  mysql:8.0

# 상태 확인
docker ps

# 로그 확인
docker logs knit_db

# 중지
docker stop knit_db

# 재시작
docker start knit_db

# 삭제
docker rm knit_db
```

### MySQL 접속

MySQL 데이터베이스 접속정보

```bash
MySQL Workbench, DBeaver, DataGrip 등 DB 접속 도구 설정:

호스트(Host): localhost 또는 127.0.0.1
포트(Port): 3306
사용자명(Username): root
비밀번호(Password): password
데이터베이스(Database): knit_db
```