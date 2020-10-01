package com.neo.nbdapi;

import com.neo.nbdapi.dao.PaginationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NbdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbdApiApplication.class, args);
    }

}
