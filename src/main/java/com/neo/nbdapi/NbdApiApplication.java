package com.neo.nbdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource(value = {"classpath:log4j.properties", "classpath:sql.properties"})
public class NbdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbdApiApplication.class, args);
    }

}
