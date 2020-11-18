package com.neo.nbdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class NbdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbdApiApplication.class, args);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", "ls /var/lib/mysql");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = "Đây là log của đức Anh:";
            String line = "";
            while ((line = reader.readLine()) != null) {
                s = s +line;
            }
            System.out.println("Đây là cái cần check =========================> " +s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
