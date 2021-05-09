FROM openjdk:11.0-jre-slim
ADD target/*.jar app.jar
ENTRYPOINT java -jar app.jar