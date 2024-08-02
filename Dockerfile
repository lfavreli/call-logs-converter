# Use the official Maven image to build the application
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project file and download dependencies
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use the official Eclipse Temurin for the runtime
FROM eclipse-temurin:21

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/call-logs-converter-1.0.0-SNAPSHOT.jar .

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "call-logs-converter-1.0.0-SNAPSHOT.jar"]