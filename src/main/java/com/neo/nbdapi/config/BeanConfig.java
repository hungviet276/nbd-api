package com.neo.nbdapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfig {

    @Bean(name = "objectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}