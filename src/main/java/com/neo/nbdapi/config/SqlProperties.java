package com.neo.nbdapi.config;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Component;

import com.neo.nbdapi.utils.Constants.ConstantParams;

@Component
public class SqlProperties {

    public SqlProperties() {
    }

    private PropertiesConfiguration configuration;

    @PostConstruct
    private void init() {
        try {
            //log4j config
            String fileLog = ConstantParams.LOG_CONFIG_FILE;
            PropertyConfigurator.configure(getClass().getResource(fileLog).getPath());
            System.out.println("Loading the log properties file: " + fileLog);
            String filePath = ConstantParams.sqlFile;
            System.out.println("Loading the properties file: " + filePath);
            configuration = new PropertiesConfiguration(filePath);
            configuration.setDelimiterParsingDisabled(true);
            configuration.setEncoding("UTF8");
            configuration.refresh();
            // Create new FileChangedReloadingStrategy to reload the properties file based
            // on the given time interval
            FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
            fileChangedReloadingStrategy.setRefreshDelay(ConstantParams.REFRESH_DELAY);
            configuration.setReloadingStrategy(fileChangedReloadingStrategy);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return (String) configuration.getString(key);
    }

    public void setProperty(String key, Object value) {
        configuration.setProperty(key, value);
    }

    public void save() {
        try {
            configuration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            configuration.load();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        configuration.reload();
    }
}