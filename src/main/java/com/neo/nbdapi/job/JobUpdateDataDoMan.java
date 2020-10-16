package com.neo.nbdapi.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.filter.FileDataFilter;
import com.neo.nbdapi.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;

/**
 * @author thanglv on 10/14/2020
 * @project NBD
 */
@Component
public class JobUpdateDataDoMan {

//    private Logger logger = LogManager.getLogger(JobUpdateDataDoMan.class);
//
//    private Marker markerDebug = MarkerManager.getMarker(Constants.LOGGER.MAKER_LOG_DEBUG);
//
//    @Value("${job.update_data_do_man.folder_data_path}")
//    private String folderDataPath;
//
//    @Value("${job.update_data_do_man.file_name_pattern}")
//    private String fileNamePattern;
//
//    @Value("${job.update_data_do_man.is_retry}")
//    private boolean isRetry;
//
//    @Autowired
//    @Qualifier("objectMapper")
//    private ObjectMapper mapper;
//
//    @Autowired
//    @Qualifier("restTemplate")
//    private RestTemplate restTemplate;
//
//    @Scheduled(cron = "* * * ? * *")
//    public void start() {
//        logger.debug("***JOB CAP NHAT DU LIEU DO MAN RUNNING ***");
////        restTemplate.getForObject();
//        File folder = new File(folderDataPath);
//        File[] listFile = folder.listFiles(new FileDataFilter(fileNamePattern));
//    }
}
