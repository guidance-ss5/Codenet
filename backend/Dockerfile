# Use official OpenJDK image
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy built jar from Maven target
COPY target/codenetbackend-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
