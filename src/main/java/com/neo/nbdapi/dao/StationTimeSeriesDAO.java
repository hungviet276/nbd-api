package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ObjectValue;
import com.neo.nbdapi.entity.StationTimeSeries;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 11/16/2020
 * @project NBD
 */
public interface StationTimeSeriesDAO {
    StationTimeSeries findByStationIdAndParameterTypeId(String stationId, Long parameterTypeId, int curTsTypeId) throws SQLException;

    List<ObjectValue> getStorageData(String storage, Integer tsId, String type, String startDate, String endDate) throws SQLException;

    List<StationTimeSeries> findByStationIdAndListParameterTypeId(String stationId, String listParameterTypeId) throws SQLException;

    List<StationTimeSeries> findByStationId(String stationId);
}
