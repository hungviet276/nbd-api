FROM openjdk:8-jre-slim
WORKDIR /nbd-api
COPY ./target/*1.0.jar nbd-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector","-jar" ,"nbd-api.jar"]