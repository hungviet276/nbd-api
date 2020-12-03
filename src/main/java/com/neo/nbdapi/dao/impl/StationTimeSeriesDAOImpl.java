package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.ObjectValue;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author thanglv on 11/16/2020
 * @project NBD
 */

@Repository
public class StationTimeSeriesDAOImpl implements StationTimeSeriesDAO {
    private Logger logger = LogManager.getLogger(StationTimeSeriesDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Override
    public StationTimeSeries findByStationIdAndParameterTypeId(String stationId, Long parameterTypeId, int curTsTypeId) throws SQLException {
        String sql = "SELECT sts.ts_id, sts.ts_name, sts.station_id, sts.ts_type_id, sts.parametertype_id, sts.parametertype_name, sts.storage, un.unit_id, un.unit_name,un.unit_code FROM station_time_series sts JOIN parameter_type pt ON sts.parametertype_id = pt.parameter_type_id JOIN unit un ON pt.unit_id = un.unit_id WHERE sts.station_id = ? AND sts.parametertype_id = ? AND sts.ts_type_id = ?";
        StationTimeSeries stationTimeSeries = null;
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationId);
            statement.setLong(2, parameterTypeId);
            statement.setInt(3, curTsTypeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stationTimeSeries = StationTimeSeries.builder()
                        .tsId(resultSet.getInt("ts_id"))
                        .tsName(resultSet.getString("ts_name"))
                        .stationId(resultSet.getString("station_id"))
                        .tsTypeId(resultSet.getInt("ts_type_id"))
                        .parameterTypeId(resultSet.getInt("parametertype_id"))
                        .parameterTypeName(resultSet.getString("parametertype_name"))
                        .storage(resultSet.getString("storage"))
                        .unitId(resultSet.getInt("unit_id"))
                        .unitName(resultSet.getString("unit_name"))
                        .unitCode(resultSet.getString("unit_code"))
                        .build();
            }
        }
        return stationTimeSeries;
    }

    @Override
    public List<ObjectValue> getStorageData(String storage, Integer tsId, String type, String startDate, String endDate) throws SQLException {
        logger.debug("parameter query storage: {}, type : {}, startDate: {}, endDate : {}", storage, type, startDate, endDate);
        List<ObjectValue> objectValues = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
        ) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableNameRequest = Strings.isEmpty(type) ? storage : storage + "_" + type;
            ResultSet table = databaseMetaData.getTables(null, null, tableNameRequest.toUpperCase(), new String[]{"TABLE"});
            if (table.next()) {
                String sql = "SELECT ts_id, value, timestamp, status, manual, warning, create_user FROM %s WHERE ts_id = ? AND timestamp >= to_date(?, 'dd/mm/yyyy HH24:mi')";
                if (endDate != null) {
                    sql = sql + " AND timestamp <= to_date(?, 'dd/mm/yyyy HH24:mi')";
                }
                sql = sql + " ORDER BY timestamp ASC";
                sql = String.format(sql, table.getString("TABLE_NAME"));
                logger.debug("sql query get data from storage: {}", sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, tsId);
                statement.setString(2, startDate);
                if (endDate != null)
                    statement.setString(3, endDate);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    ObjectValue objectValue = ObjectValue.builder()
                            .tsId(resultSet.getLong("ts_id"))
                            .value(resultSet.getFloat("value"))
                            .timestamp(resultSet.getDate("timestamp"))
                            .status(resultSet.getInt("status"))
                            .manual(resultSet.getInt("manual"))
                            .warning(resultSet.getInt("warning"))
                            .createUser(resultSet.getString("create_user"))
                            .build();
                    objectValues.add(objectValue);
                }
            } else {
                logger.debug("table storage : {} not exists", tableNameRequest);
            }
        }
        return objectValues;
    }

    @Override
    public List<StationTimeSeries> findByStationIdAndListParameterTypeId(String stationId, String listParameterTypeId) throws SQLException {
        String sql = "SELECT ts_id, ts_name, station_id, ts_type_id, parametertype_id, parametertype_name, storage FROM station_time_series WHERE station_id = ? AND parametertype_id IN (" + listParameterTypeId + ")";
        List<StationTimeSeries> listStationTimeSeries = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                StationTimeSeries stationTimeSeries = StationTimeSeries.builder()
                        .tsId(resultSet.getInt("ts_id"))
                        .tsName(resultSet.getString("ts_name"))
                        .stationId(resultSet.getString("station_id"))
                        .tsTypeId(resultSet.getInt("ts_type_id"))
                        .parameterTypeId(resultSet.getInt("parametertype_id"))
                        .parameterTypeName(resultSet.getString("parametertype_name"))
                        .storage(resultSet.getString("storage"))
                        .build();
                listStationTimeSeries.add(stationTimeSeries);
            }
        }
        return listStationTimeSeries;
    }

    @Override
    public List<StationTimeSeries> findByStationId(String stationId) {
        String sql = "SELECT station_id, ts_id, ts_name, parametertype_name,ts_type_id, parametertype_id,  parametertype_description\n" +
                "    , storage, station_no from station_time_series where station_id =?";
        List<StationTimeSeries> listStationTimeSeries = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StationTimeSeries stationTimeSeries = StationTimeSeries.builder()
                        .stationId(resultSet.getString("station_id"))
                        .tsId(resultSet.getInt("ts_id"))
                        .tsName(resultSet.getString("ts_name"))
                        .parameterTypeName(resultSet.getString("parametertype_name"))
                        .tsTypeId(resultSet.getInt("ts_type_id"))
                        .parameterTypeId(resultSet.getInt("parametertype_id"))
                        .parameterTypeDescription(resultSet.getString("parametertype_description"))
                        .storage(resultSet.getString("storage"))
                        .stationNo(resultSet.getString("station_no"))
                        .build();
                listStationTimeSeries.add(stationTimeSeries);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listStationTimeSeries;
    }

    @Override
    public List<StationTimeSeriesDTO> getValueOfStationTimeSeries(StationTimeSeriesDTO seriesDTO) {
        List<StationTimeSeriesDTO> listStationTimeSeries = new ArrayList<>();
        String sql = "SELECT * FROM " + seriesDTO.getStorage()
                + " where ts_id = ? and TIMESTAMP >= to_date(?,\'DD/MM/YYYY HH24:MI:SS\') "
                + " and TIMESTAMP <= to_date(?,\'DD/MM/YYYY HH24:MI:SS\') ";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setInt(1, seriesDTO.getTsId());
            statement.setString(2, seriesDTO.getStartDate());
            statement.setString(3, seriesDTO.getStopDate());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StationTimeSeriesDTO stationTimeSeries = StationTimeSeriesDTO.builder()
                        .id(resultSet.getInt("id"))
                        .tsId(resultSet.getInt("ts_id"))
                        .value(resultSet.getFloat("value"))
                        .timeStamp(convertDate(resultSet.getDate("TIMESTAMP")))
                        .status(resultSet.getInt("status"))
                        .manual(resultSet.getInt("manual"))
                        .warning(resultSet.getString("warning"))
                        .createUser("")
                        .build();
                listStationTimeSeries.add(stationTimeSeries);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listStationTimeSeries;
    }

    private String convertDate(Date input) {
        if (input != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String strDate = dateFormat.format(input);
            return strDate;
        }
        return "";
    }
}
