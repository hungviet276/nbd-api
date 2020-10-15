FROM openjdk:8-jre-slim
WORKDIR /sync-data-nbd
COPY  target/*-1.0.jar sync-data-nbd.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar" ,"sync-data-nbd.jar"]
