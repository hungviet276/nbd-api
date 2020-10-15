package com.neo.nbdapi.job.objdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author thanglv on 10/14/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
public class DoMan implements Serializable {

    @JsonProperty("time")
    private Date time;

    private List<String> dat;

    private long lenDat;
}
