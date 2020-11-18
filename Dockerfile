FROM store/oracle/serverjre:1.8.0_241-b07
WORKDIR /nbd-api
RUN mkdir water_level
COPY ./target/*1.0.jar nbd-api.jar
COPY ./water_level/NEO /nbd-api/water_level
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector","-jar" ,"nbd-api.jar"]