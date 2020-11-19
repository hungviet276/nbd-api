package com.neo.nbdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class NbdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbdApiApplication.class, args);
    }

}
