package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationTimeSeriesDTO {
    private String tsId;
    private float value;
    private String timeStamp;
    private long id;
    private int status;
    private int manual;
    private String warning;
    private String createUser;
    String startDate;
    String stopDate;
    String storage;
}
