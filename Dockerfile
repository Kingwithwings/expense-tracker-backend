# Step 1: Build stage using Maven and Java 17 (or change to temurin-21 if using Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -e -X

# Step 2: Lightweight runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for Spring Boot
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]