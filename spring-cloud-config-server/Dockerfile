# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
ARG JAR_FILE=target/*.jar
COPY ./target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888
# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]