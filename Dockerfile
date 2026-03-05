# ---- build stage ----
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle --no-daemon clean installDist

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/install/ktor-sample/ ./app/
EXPOSE 8080
CMD ["sh", "-c", "./app/bin/ktor-sample"]
