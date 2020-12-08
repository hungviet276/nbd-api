package com.neo.nbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigValueTypeDTO {

    private Long id;

    @JsonProperty("station_add")
    private String stationId;

    @JsonProperty("value_type_station")
    private Long valueTypeId;

    private Float min;

    private Float max;

    private Float variableTime;

    private Float variableSpatial;

    private String startDateApply;

    private String endDateApply;

    private String code;

    private Long[] stationSpatial;

}
