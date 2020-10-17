package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationValueTypeDTO implements Serializable {
    private Long id;
    private Long stationId;
    private Long valueTypeId;
    private Float min;
    private Float max;
    private Float variableTime;
    private String startDate;
    private String endDate;
    private Long[] idComrelate;
}
