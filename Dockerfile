# Java 24 base image
FROM eclipse-temurin:24-jdk

# 작업 디렉토리 생성
WORKDIR /app

# 의존성 미리 설치
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build -x test || return 0

# 전체 소스 복사
COPY . .

# 빌드
RUN ./gradlew clean build -x test

# 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/knit-api-0.0.1-SNAPSHOT.jar"]