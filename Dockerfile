# Build Stage
FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:19-jre-alpine
WORKDIR /app
COPY --from=build /app/target/forgeflow-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
