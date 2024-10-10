FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

#EXPOSE 80
#EXPOSE 11097
#
#CMD java \
#  -XX:MaxRAMPercentage=60 \
#  -jar app.jar