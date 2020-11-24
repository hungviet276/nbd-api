package com.neo.nbdapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "restTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /*  create bean apiSqlConfig use to serviceUtility, get url of soap*/
    @Bean("configSyncResource")
    public PropertiesConfiguration initBeanConfigSyncResource() throws ConfigurationException {
        PropertiesConfiguration prop = new PropertiesConfiguration();
        prop.setDelimiterParsingDisabled(true);
        prop.setEncoding("UTF8");
        prop.setPath("station_quick_report_config.properties");
        prop.load();
        prop.setReloadingStrategy(new FileChangedReloadingStrategy());

        return prop;
    }
}
