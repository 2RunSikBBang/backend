# 1단계: 빌드
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 2단계: 런타임
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 타임존/UTF-8 세팅
ENV TZ=Asia/Seoul
ENV LANG=C.UTF-8

# application.yml 파일을 런타임 이미지로 복사
COPY --from=builder /app/src/main/resources/application.yml src/main/resources/application.yml
COPY --from=builder /app/build/libs/*.jar app.jar

# HTTP 포트 노출
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
