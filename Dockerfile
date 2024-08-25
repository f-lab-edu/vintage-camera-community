# Start with a base image containing Java runtime
FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

# Copy the built JAR file
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application-email.properties /app/application-email.properties

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
