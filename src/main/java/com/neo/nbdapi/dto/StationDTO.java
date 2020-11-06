package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 11/6/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDTO implements Serializable {

    private long stationId;

    private String stationCode;

    private String stationName;

    private float elevation;

    private String image;

    private float longtitude;

    private float latitude;

    private float transMiss;

    private String address;

    private int status;

    private long areaId;

    private String areaCode;

    private String areaName;

}
