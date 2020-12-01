package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.StationTimeSeries;

import java.sql.SQLException;
import java.util.List;

public interface NormalizedDataService {

    List<StationTimeSeries> findByStationId(String stationId) throws SQLException;

    List<StationTimeSeriesDTO> getValueOfStationTimeSeries(StationTimeSeriesDTO seriesDTO);
}
