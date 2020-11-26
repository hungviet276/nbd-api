package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.Station;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationDAOImpl implements StationDAO {
    private Logger logger = LogManager.getLogger(StationDAOImpl.class);

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
    public List<Object[]> getAllStationOwnedByUser() throws SQLException {
        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.address, ar.area_name, st.is_active, ot.object_type_shortname FROM stations st JOIN areas ar ON st.area_id = ar.area_id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id WHERE st.station_id in (SELECT station_id FROM group_user_info CONNECT BY PRIOR id = group_parent START WITH id in (SELECT id FROM group_user_info WHERE id in (SELECT group_id FROM group_detail WHERE user_info_id = ?)))";
        List<Object[]> stationList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("username: {}", username);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
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
    public boolean isStationOwnedByUser(String stationId, String userId)  {
        String sql = "SELECT COUNT(1) AS total FROM group_user_info WHERE station_id = ? CONNECT BY PRIOR id = group_parent START WITH id in (SELECT id FROM group_user_info WHERE id in (SELECT group_id FROM group_detail WHERE user_info_id = ?))";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select station_id as id, station_code as code,station_name as name from stations where 1=1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and station_name like ?";
            }
            sql = sql + " and rownum < 100 and ISDEL = 0 and IS_ACTIVE = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,"%"+query+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            List<ComboBoxStr> comboBoxes = new ArrayList<>();
            while (resultSet.next()) {
                ComboBoxStr comboBox = ComboBoxStr.builder().id(resultSet.getString(1)).text(resultSet.getString(2)+"-"+resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }

    @Override
    public List<ComboBoxStr> getStationByUser() {
        List<ComboBoxStr> list = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = (User) auth.getPrincipal();
        String sql = "select s.STATION_ID,s.STATION_NAME\n" +
                "        from group_user_info gui ,group_detail gd ,stations s\n" +
                "        where gui.id = gd.group_id and gui.station_id = s.station_id\n" +
                "        and gd.user_info_id =?";
        try (Connection connection = ds.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, userLogin.getUsername());
            ResultSet rs = st.executeQuery();
            ComboBoxStr stationType = ComboBoxStr.builder()
                    .id("-1")
                    .text("Lựa chọn")
                    .build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBoxStr.builder()
                        .id(rs.getString("STATION_ID"))
                        .text(rs.getString("STATION_NAME"))
                        .build();
                list.add(stationType);
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

}
