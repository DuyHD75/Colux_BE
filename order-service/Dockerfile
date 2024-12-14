# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app

# Copy the pom.xml and install dependencies (skip tests)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src

# Build the project
RUN mvn clean package -Pprod -DskipTests

# Expose port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "target/order-service.jar"]
