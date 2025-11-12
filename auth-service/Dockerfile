# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom and common module
COPY pom.xml .
COPY common/pom.xml common/
COPY common/src common/src

# Copy auth-service
COPY auth-service/pom.xml auth-service/
COPY auth-service/src auth-service/src

# Build the application
RUN mvn clean package -DskipTests -pl auth-service -am

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /app/auth-service/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
