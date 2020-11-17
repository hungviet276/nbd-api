package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.Station;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationDAOImpl implements StationDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBoxStr> getStationComboBox(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select station_id as id, station_code as code,station_name as name from stations where 1=1";
            if (query != null && !query.equals("")) {
                sql = sql + " and station_name like ?";
            }
            sql = sql + " and rownum < 100 and ISDEL = 0 and IS_ACTIVE = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            if (query != null && !query.equals("")) {
                statement.setString(1, "%" + query + "%");
            }
            ResultSet resultSet = statement.executeQuery();
            List<ComboBoxStr> comboBoxes = new ArrayList<>();
            while (resultSet.next()) {
                ComboBoxStr comboBox = ComboBoxStr.builder().id(resultSet.getString(1)).text(resultSet.getString(2) + "-" + resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }

    @Override
    public List<Object[]> getAllStation() throws SQLException {
        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.address, ar.area_name, st.is_active, ot.object_type_shortname FROM stations st JOIN areas ar ON st.area_id = ar.area_id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id";
        List<Object[]> stationList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Object[] data = new Object[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < data.length; i++) {
                    data[i] = resultSet.getObject(i + 1);
                }
                stationList.add(data);
            }
        }
        return stationList;
    }

    @Override
    public Station findStationByStationCodeAndActiveAndIsdel(String stationCode) throws SQLException {
        String sql = "SELECT station_id, station_code, station_name, is_active, isdel FROM stations WHERE station_code = ?";
        Station station = null;
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationCode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                station = Station.builder()
                        .stationId(resultSet.getString("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .isActive(resultSet.getInt("is_active"))
                        .isDel(resultSet.getInt("isdel"))
                        .build();
            }
        }
        return station;
    }

    @Override
    public boolean isStationOwnedByUser(String stationId, String userId) throws SQLException {
        String sql = "SELECT COUNT(1) as total FROM stations st JOIN user_stations us ON st.station_id = us.station_id JOIN user_info ui ON us.user_id = ui.id WHERE st.station_id = ? AND ui.id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, stationId);
            statement.setString(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("total") > 0L;
            }
        }
        return false;
    }
}
