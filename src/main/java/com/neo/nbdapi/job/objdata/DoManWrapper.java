package com.neo.nbdapi.job.objdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author thanglv on 10/14/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoManWrapper implements Serializable {
    @JsonProperty("lsRc")
    private List<DoMan> lsRc;

    @JsonProperty("stationId")
    private String stationId;
}
