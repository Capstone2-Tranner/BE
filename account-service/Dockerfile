# 1. Base image
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 복사
ARG JAR_FILE=build/libs/account-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. 포트 노출
EXPOSE 8081

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
