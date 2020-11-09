package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConfigValueType {
    private Long id;
    private String stationId;
    private Long valueTypeId;
    private String stationName;
    private String valueTypename;
    private Float min;
    private Float max;
    private Float variableTime;
    private Float variableSpatial;
    private Date startDate;
    private Date endDate;
    private String code;
}
