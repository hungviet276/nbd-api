package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.ConfigValueTypeDAO;
import com.neo.nbdapi.dto.StationValueTypeSpatialDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.GroupMailReceive;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigValueTypeDAOImpl implements ConfigValueTypeDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getValueType(Long stationId) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select c.value_type_id, v.value_type_code , v.value_type_name from config_value_types c inner join value_types v on c.value_type_id = v.value_type_id where c.station_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, stationId);
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong("value_type_id"))
                        .text(resultSet.getString("value_type_code")+"-"+ resultSet.getString("value_type_name")).build();
                comboBoxes.add(comboBox);
            }
            return comboBoxes;
        }
    }

    @Override
    public List<ComboBox> getStationComboBox(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select c.station_id as id, s.station_code as code, s.station_name as name from stations s inner join config_value_types c on s.station_id = c.station_id where 1 = 1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and station_name like ?";
            }
            sql = sql + " and rownum < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,"%"+query+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            List<ComboBox> comboBoxes = new ArrayList<>();
            while (resultSet.next()) {
                ComboBox comboBox = ComboBox.builder().id(resultSet.getLong(1)).text(resultSet.getString(2)+"-"+resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }

    @Override
    public StationValueTypeSpatialDTO getStationValueTypeSpatial(Long idStation, Long idValueType) throws SQLException {
        StationValueTypeSpatialDTO stationValueTypeSpatialDTO = new StationValueTypeSpatialDTO();
        try (Connection connection = ds.getConnection()) {
            String sql = "select c.id, c.station_id,s.station_code, s.station_name , c.value_type_id,v.value_type_code, v.value_type_name, c.variable_spatial from config_value_types c inner join stations s on s.station_id = c.station_id inner join value_types v on v.value_type_id = c.value_type_id where c.station_id = ? and c.value_type_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idStation);
            statement.setLong(2, idValueType);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                stationValueTypeSpatialDTO = StationValueTypeSpatialDTO.
                        builder().id(resultSet.getLong("id"))
                        .stationId(resultSet.getLong("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .valueTypeId(resultSet.getLong("value_type_id"))
                        .valueTypeCode(resultSet.getString("value_type_code"))
                        .valueTypeName(resultSet.getString("value_type_name"))
                        .variableSpatial(resultSet.getInt("variable_spatial"))
                        .build();
            }
            return stationValueTypeSpatialDTO;
        }
    }
}
