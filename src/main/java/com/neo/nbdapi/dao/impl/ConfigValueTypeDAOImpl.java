package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.ConfigValueTypeDAO;
import com.neo.nbdapi.dto.ConfigStationsCommrelateDTO;
import com.neo.nbdapi.dto.ConfigValueTypeDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.StationValueTypeSpatialDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigValueTypeDAOImpl implements ConfigValueTypeDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getValueType(Long stationId, Long valueTypeId) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select c.PARAMETER_TYPE_ID, v.parameter_type_code , v.parameter_type_name, c.code from config_value_types c inner join parameter_type v on c.PARAMETER_TYPE_ID = v.parameter_type_id where c.station_id = ? and c.PARAMETER_TYPE_ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, stationId);
            statement.setLong(2, valueTypeId);
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong("parameter_type_id"))
                        .text(resultSet.getString("parameter_type_code")+"-"+ resultSet.getString("parameter_type_name")+"-"+ resultSet.getString("code")).build();
                comboBoxes.add(comboBox);
            }
            return comboBoxes;
        }
    }

    @Override
    public List<ComboBox> getStationComboBox(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select distinct c.station_id as id, s.station_code as code, s.station_name as name from stations s inner join config_value_types c on s.station_id = c.station_id where 1 = 1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and station_name like ? and ISDEL = 0 and STATUS = 1";
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
    public List<ComboBox> getStationComboBox(String query, Long idStation) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select distinct c.station_id as id, s.station_code as code, s.station_name as name from stations s inner join config_value_types c on s.station_id = c.station_id where 1 = 1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and station_name like ? and ISDEL = 0 and STATUS = 1";
            }
            sql = sql+ " and c.station_id != ?";
            sql = sql + " and rownum < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,"%"+query+"%");
                statement.setLong(2, idStation);
            } else{
                statement.setLong(1, idStation);
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
    public StationValueTypeSpatialDTO getStationValueTypeSpatial(Long idStation, Long idValueType, String code) throws SQLException {
        StationValueTypeSpatialDTO stationValueTypeSpatialDTO = new StationValueTypeSpatialDTO();
        try (Connection connection = ds.getConnection()) {
            String sql = "select c.id, c.station_id,s.station_code, s.station_name , c.parameter_type_id, v.parameter_type_code, v.parameter_type_name , c.variable_spatial, c.code from config_value_types c inner join stations s on s.station_id = c.station_id inner join parameter_type v on v.parameter_type_id = c.parameter_type_id where c.station_id = ? and c.parameter_type_id = ? and c.code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idStation);
            statement.setLong(2, idValueType);
            statement.setString(3, code);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                stationValueTypeSpatialDTO = StationValueTypeSpatialDTO.
                        builder().id(resultSet.getLong("id"))
                        .stationId(resultSet.getLong("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .valueTypeId(resultSet.getLong("parameter_type_id"))
                        .valueTypeCode(resultSet.getString("parameter_type_code"))
                        .valueTypeName(resultSet.getString("parameter_type_name"))
                        .variableSpatial(resultSet.getInt("variable_spatial"))
                        .code(resultSet.getString("code"))
                        .build();
            }
            return stationValueTypeSpatialDTO;
        }
    }

    @Override
    public DefaultResponseDTO createConfigValuetype(ConfigValueTypeDTO configValueTypeDTO) throws Exception {
        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            String sqlInsertConfig = "insert into config_value_types (ID,STATION_ID,PARAMETER_TYPE_ID,MIN,MAX,VARIABLE_TIME,VARIABLE_SPATIAL,START_APPLY_DATE,END_APPLY_DATE,CODE) values(config_value_types_seq.nextval,?,?,?,?,?,?,?,?,?)";
            String sqlInsertSpatial = "insert into config_stations_commrelate (ID, CONFIG_VALUE_TYPES_ID,CONFIG_VALUE_TYPES_PARENT) values (config_stations_commrelate_seq.nextval, ?,?)";
            String sqlGetCurrentId = "SELECT  config_value_types_seq.CURRVAL FROM dual";
            PreparedStatement stmInsertConfig = connection.prepareStatement(sqlInsertConfig);
            PreparedStatement stmGetCurrentId = connection.prepareStatement(sqlGetCurrentId);
            PreparedStatement stmInsertSpatial = connection.prepareStatement(sqlInsertSpatial);
            // thêm cấu hình trạm mới
            stmInsertConfig.setString(1,configValueTypeDTO.getStationId());
            stmInsertConfig.setLong(2,configValueTypeDTO.getValueTypeId());
            stmInsertConfig.setFloat(3,configValueTypeDTO.getMin());
            stmInsertConfig.setFloat(4,configValueTypeDTO.getMax());
            if( configValueTypeDTO.getVariableTime() == null){
                stmInsertConfig.setNull(5, Types.FLOAT);
            } else{
                stmInsertConfig.setFloat(5, configValueTypeDTO.getVariableTime());
            }
            if(configValueTypeDTO.getVariableSpatial() == null){
                stmInsertConfig.setNull(6, Types.FLOAT);
            } else{
                stmInsertConfig.setFloat(6, configValueTypeDTO.getVariableSpatial());
            }
            if(configValueTypeDTO.getStartDateApply()!=null){
                stmInsertConfig.setDate(7,new Date(configValueTypeDTO.getStartDateApply().getTime()));
            } else{
                stmInsertConfig.setDate(7,null);
            }
            if(configValueTypeDTO.getEndDateApply()!=null)
                stmInsertConfig.setDate(8,new Date(configValueTypeDTO.getEndDateApply().getTime()));
            else
                stmInsertConfig.setDate(8,null);
            stmInsertConfig.setString(9,configValueTypeDTO.getCode());
            stmInsertConfig.executeUpdate();
            Long idStationParent = null;
            ResultSet resultSet = stmGetCurrentId.executeQuery();
            if(resultSet.next()){
                idStationParent = resultSet.getLong(1);
            } else{
                throw new Exception();
            }

            Long[] configSpatial = configValueTypeDTO.getStationSpatial();
            if(configSpatial.length > 0){
                for(int i =0 ; i < configSpatial.length ; i ++){
                    stmInsertSpatial.setLong(1,configSpatial[i]);
                    stmInsertSpatial.setLong(2,idStationParent);
                    stmInsertSpatial.addBatch();
                }
                stmInsertSpatial.executeBatch();

             }
            stmInsertSpatial.close();
            stmInsertConfig.close();
            stmGetCurrentId.close();
            connection.commit();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public List<StationValueTypeSpatialDTO> getStationValueTypeSpatials(Long idConfigValueTypeParent) throws SQLException {
        List<StationValueTypeSpatialDTO> stationValueTypeSpatialDTOs = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select ct.id, s.station_id, s.station_code, s.station_name, v.parameter_type_id, v.parameter_type_code, v.parameter_type_name, ct.variable_spatial, ct.code  from config_stations_commrelate c inner join config_value_types ct on ct.id = c.config_value_types_id inner join stations s on ct.station_id = s.station_id inner join parameter_type v on v.parameter_type_id = ct.parameter_type_id where c.config_value_types_parent  = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idConfigValueTypeParent);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StationValueTypeSpatialDTO stationValueTypeSpatialDTO = StationValueTypeSpatialDTO.
                        builder().id(resultSet.getLong("id"))
                        .stationId(resultSet.getLong("station_id"))
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .valueTypeId(resultSet.getLong("parameter_type_id"))
                        .valueTypeCode(resultSet.getString("parameter_type_code"))
                        .valueTypeName(resultSet.getString("parameter_type_name"))
                        .variableSpatial(resultSet.getInt("variable_spatial"))
                        .code(resultSet.getString("code"))
                        .build();
                stationValueTypeSpatialDTOs.add(stationValueTypeSpatialDTO);
            }
            return stationValueTypeSpatialDTOs;
        }
    }



    @Override
    public DefaultResponseDTO editConfigValuetype(ConfigValueTypeDTO configValueTypeDTO, List<ConfigStationsCommrelateDTO> deletesSpatials, List<ConfigStationsCommrelateDTO> createSpatials) throws Exception {
        Connection connection = ds.getConnection();
        try  {
            connection.setAutoCommit(false);
            String sqlUpdateConfig = "update config_value_types set MIN = ? , MAX = ? , VARIABLE_TIME = ? , VARIABLE_SPATIAL = ?, START_APPLY_DATE = ?, END_APPLY_DATE = ?, CODE = ? where ID = ?";
            String sqlDeleteSpatial = "delete from config_stations_commrelate where id  = ?";
            String sqlCreateSpatial = "insert into config_stations_commrelate (ID, CONFIG_VALUE_TYPES_ID,CONFIG_VALUE_TYPES_PARENT) values (config_stations_commrelate_seq.nextval, ?,?)";
            PreparedStatement stmUpdateConfig = connection.prepareStatement(sqlUpdateConfig);
            PreparedStatement stmDeleteSpatial = connection.prepareStatement(sqlDeleteSpatial);
            PreparedStatement stmCreateSpatial = connection.prepareStatement(sqlCreateSpatial);
            // sửa các thông tin của cấu hình
            stmUpdateConfig.setFloat(1,configValueTypeDTO.getMin());
            stmUpdateConfig.setFloat(2,configValueTypeDTO.getMax());
            if( configValueTypeDTO.getVariableTime() == null){
                stmUpdateConfig.setNull(3, Types.FLOAT);
            } else{
                stmUpdateConfig.setFloat(3, configValueTypeDTO.getVariableTime());
            }
            if(configValueTypeDTO.getVariableSpatial() == null){
                stmUpdateConfig.setNull(4, Types.FLOAT);
            } else{
                stmUpdateConfig.setFloat(4, configValueTypeDTO.getVariableSpatial());
            }
            stmUpdateConfig.setDate(5,new Date(configValueTypeDTO.getStartDateApply().getTime()));
            stmUpdateConfig.setDate(6,new Date(configValueTypeDTO.getEndDateApply().getTime()));
            stmUpdateConfig.setString(7,configValueTypeDTO.getCode());
            stmUpdateConfig.setLong(8,configValueTypeDTO.getId());
            stmUpdateConfig.executeUpdate();

            // thực hiện xoá những bản ghi đã bị xóa ở table

            for (ConfigStationsCommrelateDTO tmp : deletesSpatials){
                stmDeleteSpatial.setLong(1, tmp.getId());
                stmDeleteSpatial.addBatch();
            }
            if(deletesSpatials.size()>0){
                stmDeleteSpatial.executeBatch();
            }

            for (ConfigStationsCommrelateDTO tmp : createSpatials){
                stmCreateSpatial.setLong(1, tmp.getConfigValueTypeId());
                stmCreateSpatial.setLong(2,tmp.getConfigValueTypeParent());
                stmCreateSpatial.addBatch();
            }
            if(createSpatials.size()>0){
                stmCreateSpatial.executeBatch();
            }
            stmCreateSpatial.close();
            stmDeleteSpatial.close();
            stmUpdateConfig.close();
            connection.commit();
        } finally {
            connection.close();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public List<ConfigStationsCommrelateDTO> getListConfigStationsCommrelateDTO(Long parentId) throws Exception {
        List<ConfigStationsCommrelateDTO> configStationsCommrelateDTOs = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select id, CONFIG_VALUE_TYPES_ID, config_value_types_parent from config_stations_commrelate where config_value_types_parent = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, parentId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ConfigStationsCommrelateDTO configStationsCommrelateDTO = ConfigStationsCommrelateDTO.
                        builder().id(resultSet.getLong("id"))
                        .configValueTypeId(resultSet.getLong("CONFIG_VALUE_TYPES_ID"))
                        .configValueTypeParent(resultSet.getLong("config_value_types_parent"))
                        .build();
                configStationsCommrelateDTOs.add(configStationsCommrelateDTO);
            }
            return configStationsCommrelateDTOs;
        }
    }

    @Override
    public DefaultResponseDTO deleteConfigValuetype(Long id) throws Exception {
        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            String sqlDeleteConfig = "delete from config_stations_commrelate where config_value_types_parent = ?";
            String sqlDeteSpatial = "delete from config_value_types where id = ?";
            PreparedStatement stmDeleteConfig = connection.prepareStatement(sqlDeleteConfig);
            PreparedStatement stmDeteSpatial = connection.prepareStatement(sqlDeteSpatial);

            stmDeteSpatial.setLong(1,id);
            stmDeteSpatial.executeUpdate();

            stmDeleteConfig.setLong(1,id);
            stmDeleteConfig.executeUpdate();

            stmDeleteConfig.close();
            stmDeteSpatial.close();
            connection.commit();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }
}
