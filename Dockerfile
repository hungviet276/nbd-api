FROM openjdk:8-jre-slim
WORKDIR /nbd-api
COPY build/libs/nbd-api-1.0.jar nbd-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar" ,"nbd-api.jar"]