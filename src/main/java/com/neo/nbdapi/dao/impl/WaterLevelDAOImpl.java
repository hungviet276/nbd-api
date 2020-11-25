package com.neo.nbdapi.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.WaterLevelDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.VariableTime;
import com.neo.nbdapi.entity.VariablesSpatial;
import com.neo.nbdapi.entity.WaterLevel;
import com.neo.nbdapi.entity.WaterLevelExecute;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WaterLevelDAOImpl implements WaterLevelDAO {

    private Logger logger = LogManager.getLogger(WaterLevelDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Override
    public List<Object> queryInformation(WaterLevelVM waterLevelVM) throws SQLException {
        List<Object> list = new ArrayList<>();
        VariableTime variableTime = null;
        List<VariablesSpatial> variablesSpatials = new ArrayList<>();
        Float nearest = null;
        Connection connection = ds.getConnection();
        PreparedStatement stmMinMaxVariableTime = null;
        PreparedStatement stmVariableSpatial = null;
        PreparedStatement stmValueNearest = null;
        try{
            connection.setAutoCommit(false);
            String sqlMinMaxVariableTime =
                    "select max(c.min) min, min(c.max) max, min(c.variable_time) variable_time, min(c.variable_spatial) as variable_spatial from config_value_types c " +
                    "where id in " +
                    "(select c.id from config_value_types c " +
                    "    where station_id = ? and c.start_apply_date <= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF') \n" +
                    "    and c.end_apply_date+1 >= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF')" +
                    ")  " +
                    "and c.parameter_type_id = "+ Constants.WATER_LEVEL.PARAMETER_TYPE_ID;
            String sqlVariableSpatial =
                    "select STATION_ID,VARIABLE_SPATIAL, max(VALUE) as max, min(value) as min from " +
                            "(select tmp.station_id, tmp.variable_spatial, tmp.ts_id,w.value from " +
                            "    (select ct.station_id, ct.variable_spatial, ss.ts_id from config_value_types ct inner join station_time_series ss on ss.station_id = ct.station_id " +
                            "        where ct.id in " +
                            "            (select cc.config_value_types_id from config_value_types c inner join config_stations_commrelate cc on cc.config_value_types_parent = c.id where station_id = ? ) " +
                            "             and ct.parameter_type_id =  " +Constants.WATER_LEVEL.PARAMETER_TYPE_ID+
                            "             and ct.start_apply_date <= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF') " +
                            "             and ct.end_apply_date +1 > TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF')" +
                            "    ) tmp  " +
                            "        inner join water_level w on w.ts_id = tmp.ts_id where w.timestamp >= trunc(TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF') -1) " +
                            "        and w.timestamp <trunc(TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF') +1) " +
                            ") group by STATION_ID, VARIABLE_SPATIAL";
            String sqlValueNearest = "select * from (select w.value from water_level w where w.ts_id in (select tmp.ts_id from water_level tmp  where tmp.id = ?) and w.timestamp <  (select timestamp from water_level where id = ?) and w.warning = 1 order by w.timestamp desc)where  rownum = 1";
            logger.info("WaterLevelDAOImpl : {}",sqlMinMaxVariableTime);
            logger.info("WaterLevelDAOImpl : {}",sqlVariableSpatial);
            logger.info("WaterLevelDAOImpl : {}",sqlValueNearest);

            stmMinMaxVariableTime = connection.prepareStatement(sqlMinMaxVariableTime);
            stmVariableSpatial = connection.prepareStatement(sqlVariableSpatial);
            stmValueNearest = connection.prepareStatement(sqlValueNearest);

            stmMinMaxVariableTime.setString(1, waterLevelVM.getStationId());
            stmMinMaxVariableTime.setString(2, waterLevelVM.getTimestamp());
            stmMinMaxVariableTime.setString(3, waterLevelVM.getTimestamp());
            ResultSet resultSetMinMaxVariableTime = stmMinMaxVariableTime.executeQuery();
            while(resultSetMinMaxVariableTime.next()){
                variableTime = VariableTime.builder()
                        .min(resultSetMinMaxVariableTime.getFloat("min"))
                        .max(resultSetMinMaxVariableTime.getFloat("max"))
                        .variableTime(resultSetMinMaxVariableTime.getFloat("variable_time"))
                        .variableSpatial(resultSetMinMaxVariableTime.getFloat("variable_spatial"))
                        .build();
            }
            stmVariableSpatial.setString(1, waterLevelVM.getStationId());
            stmVariableSpatial.setString(2, waterLevelVM.getTimestamp());
            stmVariableSpatial.setString(3, waterLevelVM.getTimestamp());
            stmVariableSpatial.setString(4, waterLevelVM.getTimestamp());
            stmVariableSpatial.setString(5, waterLevelVM.getTimestamp());

            ResultSet resultSetVariableSpatial = stmVariableSpatial.executeQuery();
            while(resultSetVariableSpatial.next()){
                VariablesSpatial variablesSpatial = VariablesSpatial.builder()
                        .max(resultSetVariableSpatial.getFloat("max"))
                        .min(resultSetVariableSpatial.getFloat("min"))
                        .variableSpatial(resultSetVariableSpatial.getFloat("VARIABLE_SPATIAL"))
                        .build();
                variablesSpatials.add(variablesSpatial);
            }
            stmValueNearest.setLong(1,waterLevelVM.getId());
            stmValueNearest.setLong(2,waterLevelVM.getId());
            ResultSet resultSetValueNearest = stmValueNearest.executeQuery();

            while (resultSetValueNearest .next()){
                nearest = resultSetValueNearest.getFloat("value");
            }

            list.add(variableTime);
            list.add(variablesSpatials);
            list.add(nearest);

            connection.commit();
        } catch ( Exception e){
            return null;
        } finally {
             if(stmMinMaxVariableTime!=null){
                 stmMinMaxVariableTime.close();
             }
             if(stmVariableSpatial != null){
                 stmVariableSpatial.close();
             }
             if(stmValueNearest!=null){
                 stmValueNearest.close();
             }
             if(connection!=null){
                 connection.close();
             }
        }
        return list;
    }

    @Override
    public DefaultResponseDTO updateWaterLevel(WaterLevelVM waterLevelVM) throws SQLException {
        Connection connection = ds.getConnection();
        try {
            connection.setAutoCommit(false);
            String sql = "update water_level set value = ? , warning = ?, create_user = ? where id = ?";
            String sql1h = "update water_level_1h set value = ? , warning = ?, create_user = ? where parent_id = ?";
            String sql3h = "update water_level_3h set value = ? , warning = ?, create_user = ? where parent_id = ?";
            String sql24h = "update water_level_24h set value = ? , warning = ?, create_user = ? where parent_id = ?";
            logger.debug("JDBC execute query : {}", sql);

            PreparedStatement statement = connection.prepareStatement(sql);

            PreparedStatement statement1h = connection.prepareStatement(sql1h);

            PreparedStatement statement3h = connection.prepareStatement(sql3h);

            PreparedStatement statement24 = connection.prepareStatement(sql24h);

            statement.setFloat(1, waterLevelVM.getValue());
            statement.setInt(2, waterLevelVM.getWarning());
            statement.setString(3, waterLevelVM.getUser());
            statement.setLong(4, waterLevelVM.getId());
            statement.executeUpdate();


            statement1h.setFloat(1, waterLevelVM.getValue());
            statement1h.setInt(2, waterLevelVM.getWarning());
            statement1h.setString(3, waterLevelVM.getUser());
            statement1h.setLong(4, waterLevelVM.getId());
            statement1h.executeUpdate();

            statement3h.setFloat(1, waterLevelVM.getValue());
            statement3h.setInt(2, waterLevelVM.getWarning());
            statement3h.setString(3, waterLevelVM.getUser());
            statement3h.setLong(4, waterLevelVM.getId());
            statement3h.executeUpdate();

            statement24.setFloat(1, waterLevelVM.getValue());
            statement24.setInt(2, waterLevelVM.getWarning());
            statement24.setString(3, waterLevelVM.getUser());
            statement24.setLong(4, waterLevelVM.getId());
            statement24.executeUpdate();
            if(statement != null){
                statement.close();
            }
            if(statement1h != null){
                statement1h.close();
            }
            if(statement3h != null){
                statement3h.close();
            }
            if(statement24 != null){
                statement24.close();
            }
            connection.commit();
        } catch (Exception e){
            connection.rollback();
            throw  e;
        } finally {
            if(connection!=null){
                connection.close();
            }
        }
        return DefaultResponseDTO.builder().message("Chỉnh sửa thành công").status(waterLevelVM.getWarning()).build();
    }

    @Override
    public List<WaterLevel> getListWaterLevelByTime(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException {
        List<WaterLevel> waterLevels = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {

            StringBuilder sql = new StringBuilder("select w.id, w.ts_id, w.value, w.timestamp, w.status, w.manual, w.warning, w.create_user  from water_Level w inner join station_time_series s on s.ts_id = w.ts_id where 1 = 1 ");

            sql.append(" AND s.station_id = ? ");

            sql.append(" AND w.timestamp  >=  to_timestamp(?, 'DD/MM/YYYY') ");

            sql.append(" AND w.timestamp -1 <  to_timestamp(?, 'DD/MM/YYYY') ");

            sql.append(" ORDER BY w.timestamp ");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            stmt.setString(1, waterLevelExecutedVM.getStationId());
            stmt.setString(2,waterLevelExecutedVM.getStartDate());
            stmt.setString(3,waterLevelExecutedVM.getEndDate());
            ResultSet resultSetListData = stmt.executeQuery();

            while (resultSetListData.next()) {
                WaterLevel waterLevel = WaterLevel.builder().
                        id(resultSetListData.getLong("id"))
                        .tsId(resultSetListData.getLong("ts_id"))
                        .value(resultSetListData.getFloat("value"))
                        .timestamp(resultSetListData.getString("timestamp"))
                        .status(resultSetListData.getInt("status"))
                        .manual(resultSetListData.getInt("manual"))
                        .warning(resultSetListData.getInt("warning"))
                        .createUser(resultSetListData.getString("create_user"))
                        .build();

                waterLevels.add(waterLevel);
            }
        }
        return waterLevels;
    }

    @Override
    public List<WaterLevelExecute> executeWaterLevel(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException{
        List<WaterLevelExecute> waterLevels = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {

            String table = "";

            if(waterLevelExecutedVM.getHours() == 1){
                table = "water_level_1h";
            } else if(waterLevelExecutedVM.getHours() == 3){
                table = "water_level_3h";
            } else if(waterLevelExecutedVM.getHours() == 24){
                table = "water_level_24h";
            }

            StringBuilder sql = new StringBuilder("select w.timestamp,  w.value from "+table+" w inner join station_time_series s on s.ts_id = w.ts_id where 1 = 1 ");
            sql.append(" AND s.station_id = ? ");
            sql.append(" AND w.timestamp  >=  to_timestamp(?, 'DD/MM/YYYY') ");
            sql.append(" AND w.timestamp -1 <  to_timestamp(?, 'DD/MM/YYYY') ");
            sql.append(" ORDER BY w.timestamp ");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            stmt.setString(1, waterLevelExecutedVM.getStationId());
            stmt.setString(2,waterLevelExecutedVM.getStartDate());
            stmt.setString(3,waterLevelExecutedVM.getEndDate());
            ResultSet resultSetListData = stmt.executeQuery();

            while (resultSetListData.next()) {
                WaterLevelExecute waterLevel = WaterLevelExecute.builder()
                        .value(resultSetListData.getFloat("value"))
                        .timestamp(resultSetListData.getString("timestamp"))
                        .build();
                waterLevels.add(waterLevel);
            }
        }
        return waterLevels;
    }

}
