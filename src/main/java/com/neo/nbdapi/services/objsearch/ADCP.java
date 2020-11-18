package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.nbdapi.config.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Date;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ADCP {

    private Date timeStart;

    private Date timeEnd;

    private Date timeAvg;

    private Float waterLevelStart;

    private Float waterLevelEnd;

    private Float waterLevelAvg;

    private Float speedAvg;

    private Float speedMax;

    private Float deepAvg;

    private Float widthRiver;

    private Float waterFlow;

    private String note;

    private File linkFile;

    private String username;
}
