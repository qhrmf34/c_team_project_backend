FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/hotel_project-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Set command to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
