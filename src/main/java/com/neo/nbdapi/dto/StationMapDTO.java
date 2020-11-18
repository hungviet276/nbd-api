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
public class StationMapDTO implements Serializable {

    private float longtitude;

    private float latitude;

    private String stationId;

    private String stationCode;

    private String stationName;

    private float elevation;

    private String image;

    private float transMiss;

    private String address;

    private String areaName;

    private int isActive;

    private String stationTypeName;
}
