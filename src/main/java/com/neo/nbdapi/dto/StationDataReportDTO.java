package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.ObjectValue;
import com.neo.nbdapi.entity.Station;

import java.util.List;

/**
 * @author thanglv on 11/18/2020
 * @project NBD
 */
public class StationDataReportDTO {
    private List<TimeSeriesDataDTO> listTimeSeriesData;
    private Station station;
}
