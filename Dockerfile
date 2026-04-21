FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle* .
COPY build.gradle* .
COPY blackjack-core/ blackjack-core/
COPY blackjack-web/ blackjack-web/
COPY frontend/ frontend/

RUN chmod +x gradlew && ./gradlew :blackjack-web:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/blackjack-web/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]