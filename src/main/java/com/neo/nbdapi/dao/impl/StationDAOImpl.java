package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.Station;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.DateUtils;
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
import java.util.Calendar;
import java.util.Date;
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
                sql = sql + " and UPPER(station_name) like ?";
            }
            sql = sql + " and rownum < 100 and ISDEL = 0 and IS_ACTIVE = 1 order by station_code";
            PreparedStatement statement = connection.prepareStatement(sql);
            if (query != null && !query.equals("")) {
                statement.setString(1, "%" + query.toUpperCase() + "%");
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

    // method lay csv data station cua user
    // method cu tam thoi chua dung
//    @Override
//    public List<Object[]> getCSVAllStationOwnedByUser(String username) throws SQLException {
//        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.address, ar.area_name, st.is_active, ot.object_type_shortname FROM group_user_info gui JOIN group_detail gd ON gd.group_id = gui.id JOIN stations st ON st.station_id = gui.station_id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id JOIN areas ar ON st.area_id = ar.area_id WHERE gd.user_info_id = ?";
//        List<Object[]> stationList = new ArrayList<>();
//        try (
//                Connection connection = ds.getConnection();
//                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//        ) {
//            statement.setString(1, username);
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                Object[] data = new Object[resultSet.getMetaData().getColumnCount()];
//                for (int i = 0; i < data.length; i++) {
//                    data[i] = resultSet.getObject(i + 1);
//                }
//                stationList.add(data);
//            }
//        }
//        return stationList;
//    }

    @Override
    public Station findStationByStationCodeAndActiveAndIsdel(String stationCode) throws SQLException {
        String sql = "SELECT station_id, station_code, station_name, is_active, isdel, cur_ts_type_id FROM stations WHERE station_code = ?";
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
                        .curTsTypeId(resultSet.getInt("cur_ts_type_id"))
                        .build();
            }
        }
        return station;
    }

    @Override
    public boolean isStationOwnedByUser(String stationId, String userId) {
        String sql = "SELECT COUNT(1) AS total FROM group_user_info gui JOIN group_detail gd ON gd.group_id = gui.id JOIN user_info ui ON ui.id = gd.user_info_id JOIN stations st ON st.station_id = gui.station_id WHERE gd.user_info_id = ? AND st.station_id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, userId);
            statement.setString(2, stationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("total") > 0L;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // method lay csv data station cua user theo object type
    // method cu tam thoi chua dung
//    @Override
//    public List<Object[]> getCSVAllStationOwnedByUserAndObjectType(String username, String objectType) throws SQLException {
//        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.address, ar.area_name, st.is_active, ot.object_type_shortname FROM group_user_info gui JOIN group_detail gd ON gd.group_id = gui.id JOIN stations st ON st.station_id = gui.station_id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id JOIN areas ar ON st.area_id = ar.area_id WHERE gd.user_info_id = ? AND ot.object_type LIKE '" + objectType + "%'";
//        List<Object[]> stationList = new ArrayList<>();
//        try (
//                Connection connection = ds.getConnection();
//                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//        ) {
//            statement.setString(1, username);
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                Object[] data = new Object[resultSet.getMetaData().getColumnCount()];
//                for (int i = 0; i < data.length; i++) {
//                    data[i] = resultSet.getObject(i + 1);
//                }
//                stationList.add(data);
//            }
//        }
//        return stationList;
//    }

    @Override
    public List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select station_id as id, station_code as code,station_name as name from stations where 1=1 and station_id in ('"+ Constants.WATER_LEVEL.ID_PHU_QUOC +"', '"+Constants.WATER_LEVEL.ID_HA_TIEN+"', '"+Constants.WATER_LEVEL.ID_GANH_HAO+"')";
            if (query != null && !query.equals("")) {
                sql = sql + " and UPPER(station_name) like ?";
            }
            sql = sql + " and rownum < 100 and ISDEL = 0 and IS_ACTIVE = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            if (query != null && !query.equals("")) {
                statement.setString(1, "%" + query.toUpperCase() + "%");
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
    public List<ComboBoxStr> getStationByUser() {
        List<ComboBoxStr> list = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = (User) auth.getPrincipal();
        String sql = "select s.STATION_ID,s.STATION_NAME, s.STATION_CODE\n" +
                "        from group_user_info gui ,group_detail gd ,stations s\n" +
                "        where gui.id = gd.group_id and gui.station_id = s.station_id\n" +
                "        and gd.user_info_id =?";
//        String sql = "SELECT station_id, station_name " +
//                "FROM stations " +
//                "where station_id in (SELECT DISTINCT station_id FROM station_time_series WHERE parametertype_name is not null)";
        try (Connection connection = ds.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, userLogin.getUsername());
            ResultSet rs = st.executeQuery();
            ComboBoxStr stationType;
            while (rs.next()) {
                stationType = ComboBoxStr.builder()
                        .id(rs.getString("STATION_ID"))
                        .text(rs.getString("STATION_NAME"))
                        .moreInfo(rs.getString("STATION_CODE"))
                        .build();
                list.add(stationType);
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    @Override
    public Station getStationById(String stationId) {
        String sql = "SELECT station_id, station_code, station_name FROM stations where station_id = ?";
        Station station = null;
        try (Connection connection = ds.getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, stationId);
            ResultSet rs = st.executeQuery();
            ComboBoxStr stationType;
            while (rs.next()) {
                station = Station
                        .builder()
                        .stationId(rs.getString("station_id"))
                        .stationName(rs.getString("station_name"))
                        .stationCode(rs.getString("station_code"))
                        .build();
            }
            rs.close();
            return station;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public List<StationMapDTO> getAllStationOwnedByUserAndObjectType(String username, String objectType) throws SQLException {
        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.is_active, ot.object_type_shortname , ar.area_name, dst.district_name, prv.province_name, st.address, (SELECT COUNT(1) from warning_manage_stations wms JOIN warning_recipents wr ON wr.manage_warning_stations = wms.id JOIN notification_history nh ON nh.warning_recipents_id = wr.id WHERE wms.station_id = st.station_id AND nh.push_timestap >= to_date(?, 'dd/mm/yyyy') AND nh.push_timestap < to_date(?, 'dd/mm/yyyy')) AS count_warning FROM stations st JOIN group_user_info gui ON st.station_id = gui.station_id JOIN group_detail gd ON gd.group_id = gui.id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id LEFT JOIN areas ar ON st.area_id = ar.area_id LEFT JOIN districts dst ON dst.district_id = st.district_id LEFT JOIN provinces prv ON prv.province_id = st.province_id WHERE st.isdel = 0 AND st.is_active = 1 AND gd.user_info_id = ? AND ot.object_type LIKE '" + objectType + "%'";
        List<StationMapDTO> stationList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ) {
            statement.setString(1, DateUtils.getStringFromDateFormat(new Date(), "dd/MM/yyyy"));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            statement.setString(2, DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy"));
            statement.setString(3, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StationMapDTO stationMapDTO = StationMapDTO
                        .builder()
                        .stationId(resultSet.getString("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .image(resultSet.getString("image"))
                        .longtitude(resultSet.getFloat("longtitude"))
                        .latitude(resultSet.getFloat("latitude"))
                        .transMiss(resultSet.getInt("trans_miss"))
                        .isActive(resultSet.getInt("is_active"))
                        .objectTypeShortName(resultSet.getString("object_type_shortname"))
                        .areaName(resultSet.getString("area_name"))
                        .districtName(resultSet.getString("district_name"))
                        .provinceName(resultSet.getString("province_name"))
                        .address(resultSet.getString("address"))
                        .countWarning(resultSet.getInt("count_warning"))
                        .build();
                        stationList.add(stationMapDTO);
            }
        }
        return stationList;
    }

    @Override
    public List<StationMapDTO> getAllStationOwnedByUser(String username) throws SQLException {
        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.is_active, ot.object_type_shortname , ar.area_name, dst.district_name, prv.province_name, st.address, (SELECT COUNT(1) from warning_manage_stations wms JOIN warning_recipents wr ON wr.manage_warning_stations = wms.id JOIN notification_history nh ON nh.warning_recipents_id = wr.id WHERE wms.station_id = st.station_id AND nh.push_timestap >= to_date(?, 'dd/mm/yyyy') AND nh.push_timestap < to_date(?, 'dd/mm/yyyy')) AS count_warning FROM stations st JOIN group_user_info gui ON st.station_id = gui.station_id JOIN group_detail gd ON gd.group_id = gui.id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id LEFT JOIN areas ar ON st.area_id = ar.area_id LEFT JOIN districts dst ON dst.district_id = st.district_id LEFT JOIN provinces prv ON prv.province_id = st.province_id WHERE st.isdel = 0 AND st.is_active = 1 AND gd.user_info_id = ?";
        List<StationMapDTO> stationList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ) {
            statement.setString(1, DateUtils.getStringFromDateFormat(new Date(), "dd/MM/yyyy"));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            statement.setString(2, DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy"));
            statement.setString(3, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StationMapDTO stationMapDTO = StationMapDTO
                        .builder()
                        .stationId(resultSet.getString("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .image(resultSet.getString("image"))
                        .longtitude(resultSet.getFloat("longtitude"))
                        .latitude(resultSet.getFloat("latitude"))
                        .transMiss(resultSet.getInt("trans_miss"))
                        .isActive(resultSet.getInt("is_active"))
                        .objectTypeShortName(resultSet.getString("object_type_shortname"))
                        .areaName(resultSet.getString("area_name"))
                        .districtName(resultSet.getString("district_name"))
                        .provinceName(resultSet.getString("province_name"))
                        .address(resultSet.getString("address"))
                        .countWarning(resultSet.getInt("count_warning"))
                        .build();
                stationList.add(stationMapDTO);
            }
        }
        return stationList;
    }
}
