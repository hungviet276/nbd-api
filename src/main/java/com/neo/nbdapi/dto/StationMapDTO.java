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

    private Float longtitude;

    private Float latitude;

    private String stationId;

    private String stationCode;

    private String stationName;

    private Float elevation;

    private String image;

    private Integer transMiss;

    private Integer isActive;

    private String objectTypeShortName;

    private String areaName;

    private String provinceName;

    private String districtName;

    private String address;
}
