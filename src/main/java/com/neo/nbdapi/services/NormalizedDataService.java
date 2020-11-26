package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.StationTimeSeries;

import java.sql.SQLException;
import java.util.List;

public interface NormalizedDataService {

    List<ComboBoxStr> getAllStationOwnedByUser() throws SQLException;

    List<StationTimeSeries> findByStationId(String stationId) throws SQLException;
}
