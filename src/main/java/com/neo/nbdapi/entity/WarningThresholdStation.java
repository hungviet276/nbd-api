package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningThresholdStation implements Serializable {

    private Long id;

    private Long stationId;

    private Long parameterId;

    private String stationName;

    private String parameterName;

    private Float valueLevel1;

    private Float valueLevel2;

    private Float valueLevel3;

    private Float valueLevel4;

    private Float valueLevel5;

    private String thresholdCode;
}
