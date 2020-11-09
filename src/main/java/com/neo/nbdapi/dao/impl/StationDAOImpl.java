package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public List<StationMapDTO> getAllStationMap() throws SQLException {
        String sql = "SELECT st.station_id, st.station_code, st.station_name, st.image, st.longtitude, st.latitude, st.trans_miss, st.address, ar.area_name, st.is_active, ot.object_type_shortname FROM stations st JOIN areas ar ON st.area_id = ar.area_id JOIN stations_object_type sot ON st.station_id = sot.station_id JOIN object_type ot ON sot.object_type_id = ot.object_type_id";
        List<StationMapDTO> stationMapDTOList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         ) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                StationMapDTO stationMapDTO = StationMapDTO.builder()
                        .stationId(resultSet.getLong("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .image(resultSet.getString("image"))
                        .longtitude(resultSet.getFloat("longtitude"))
                        .latitude(resultSet.getFloat("latitude"))
                        .transMiss(resultSet.getFloat("trans_miss"))
                        .address(resultSet.getString("address"))
                        .areaName(resultSet.getString("area_name"))
                        .isActive(resultSet.getInt("is_active"))
                        .stationTypeName(resultSet.getString("object_type_shortname"))
                        .build();
                stationMapDTOList.add(stationMapDTO);
            }
        }
        return stationMapDTOList;
    }
}
