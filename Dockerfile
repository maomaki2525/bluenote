# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 先にGradle wrapper周りだけコピー（キャッシュ効く）
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon clean installDist

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/install/ktor-sample/ ./app/

EXPOSE 8080
CMD ["sh", "-c", "./app/bin/ktor-sample"]