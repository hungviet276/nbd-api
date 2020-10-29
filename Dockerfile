FROM store/oracle/serverjre:1.8.0_241-b07
WORKDIR /nbd-api
COPY ./target/*1.0.jar nbd-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar" ,"nbd-api.jar"]