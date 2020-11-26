package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.entity.ObjectValue;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
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
    public StationTimeSeries findByStationIdAndParameterTypeId(String stationId, Long parameterTypeId) throws SQLException {
        String sql = "SELECT sts.ts_id, sts.ts_name, sts.station_id, sts.ts_type_id, sts.parametertype_id, sts.parametertype_name, sts.storage, un.unit_id, un.unit_name,un.unit_code FROM station_time_series sts JOIN parameter_type pt ON sts.parametertype_id = pt.parameter_type_id JOIN unit un ON pt.unit_id = un.unit_id WHERE sts.station_id = ? AND sts.parametertype_id = ?";
        StationTimeSeries stationTimeSeries = null;
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationId);
            statement.setLong(2, parameterTypeId);
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
    public List<ObjectValue> getStorageData(String storage, String type, String startDate, String endDate) throws SQLException {
        List<ObjectValue> objectValues = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
        ) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableNameRequest = Strings.isEmpty(type)? storage : storage + type;
            ResultSet table = databaseMetaData.getTables(null, null, tableNameRequest.toUpperCase(), new String[]{"TABLE"});
            if (table.next()) {
                String sql = "SELECT ts_id, value, timestamp, status, manual, warning, create_user FROM %s WHERE timestamp >= to_date(?, 'dd/mm/yyyy HH24:mi')";
                if (endDate != null) {
                    sql = sql + " AND timestamp <= to_date(?, 'dd/mm/yyyy HH24:mi')";
                }
                sql = String.format(sql, table.getString("TABLE_NAME"));
                logger.debug("sql query station timeseries: {}", sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, startDate);
                if (endDate != null)
                    statement.setString(2, endDate);
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
            }
        }
        return objectValues;
    }

    @Override
    public List<StationTimeSeries> findByStationIdAndListParameterTypeId(String stationId, String listParameterTypeId) throws SQLException {
        String sql = "SELECT ts_id, ts_name, station_id, ts_type_id, parametertype_id, parametertype_name, storage FROM station_time_series WHERE station_id = ? AND parametertype_id IN ("+ listParameterTypeId +")";
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
        String sql = "SELECT ts_id, ts_name, parametertype_name, parametertype_id, ts_type_id, parametertype_description\n" +
                "    , station_no, storage from station_time_series where station_id =?";
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
}
