FROM openjdk:8-jre-slim
WORKDIR /nbd-api
COPY ./target/*.jar /nbd-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar" ,"nbd-api.jar"]
