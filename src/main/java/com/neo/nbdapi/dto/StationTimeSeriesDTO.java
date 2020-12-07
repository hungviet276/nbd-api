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
    private int tsId;
    private float value;
    private String timeStamp;
    private long id;
    private int status;
    private int manual;
    private String warning;
    private String createUser;
    private String startDate;
    private String stopDate;
    private String storage;
    private String stationId;
    private String stationCode;

    @Override
    public String toString() {
        return "StationTimeSeriesDTO ==> tsId : " + tsId + "; getStartDate: " + startDate + "; stopDate: " + stopDate;
    }
}
