package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.WarningManagerStationDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.WarningManagerDetailDTO;
import com.neo.nbdapi.dto.WarningManagerStationDTO;
import com.neo.nbdapi.dto.WarningMangerDetailInfoDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerStrVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WarningManagerStationDAOImpl implements WarningManagerStationDAO {
    private Logger logger = LogManager.getLogger(WarningManagerStationDAOImpl.class);
    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerStrVM selectVM) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select DISTINCT pt.parameter_type_id, pt.parameter_type_code, pt.parameter_type_name from warning_threshold_value wv inner join parameter_type pt on pt.parameter_type_id = wv.parameter_type_id where wv.station_id = ?";
            if(selectVM.getTerm()!=null){
                sql+="  and (pt.parameter_type_code like ? or pt.parameter_type_name like ?) ";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            if(selectVM.getId()==null){
                return comboBoxes;
            }
            statement.setString(1, selectVM.getId());
            if(selectVM.getTerm()!=null){
                statement.setString(2, "%"+selectVM.getTerm()+"%");
                statement.setString(3, "%"+selectVM.getTerm()+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong("parameter_type_id"))
                        .text(resultSet.getString("parameter_type_code")+"-"+resultSet.getString("parameter_type_name")).build();
                comboBoxes.add(comboBox);
            }
            return comboBoxes;
        }
    }

    @Override
    public List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select wt.id, wt.code from warning_threshold wt inner join warning_threshold_value wv on wt.warning_threshold_value_id = wv.id where wv.parameter_type_id = ?";
            if(selectVM.getTerm()!=null){
                sql+="  and wt.code like ? ";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            if(selectVM.getId()==null){
                return comboBoxes;
            }
            statement.setLong(1, selectVM.getId());
            if(selectVM.getTerm()!=null){
                statement.setString(2, "%"+selectVM.getTerm()+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong("id"))
                        .text(resultSet.getString("code")).build();
                comboBoxes.add(comboBox);
            }
            return comboBoxes;
        }
    }

    @Override
    public WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException {
        String sql = "select level_warning, level_clean from warning_threshold where id = ?";
        logger.info("sql get WarningThresholdINF : {}", sql);
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idThreshold);
            ResultSet resultSet = statement.executeQuery();
            WarningThresholdINF warningThresholdINF = null;
            while (resultSet.next()) {
                warningThresholdINF = WarningThresholdINF.builder()
                        .warningThreshold(resultSet.getInt("level_warning"))
                        .warningThresholdCancel(resultSet.getInt("level_clean")).build();
            }
            return warningThresholdINF;
        }
    }

    @Override
    public DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException {
        String sqlCreateWarningManageStation = "insert into warning_manage_stations(id, code, name, description, content, color, icon, station_id, created_by, created_at) values (WARNING_MANAGER_STATION_SEQ.nextval,?,?,?,?,?,?,?,?,sysdate)";
        String sqlCreateWarningManagerDetail = "insert into warning_manage_detail(id, warning_manage_station_id, warning_threshold_id, created_by, created_at) values (WARNING_MANAGER_DETAIL_SEQ.nextval, WARNING_MANAGER_STATION_SEQ.CURRVAL,?,?,sysdate)";

        logger.info("WarningManagerStationDAOImpl sql : {}",sqlCreateWarningManageStation);

        logger.info("WarningManagerStationDAOImpl sql : {}",sqlCreateWarningManagerDetail);

        Connection connection = ds.getConnection();
        try{
            connection.setAutoCommit(false);
            PreparedStatement stmCreateWarningManageStation = connection.prepareStatement(sqlCreateWarningManageStation);
            PreparedStatement stmCreateWarningManagerDetail = connection.prepareStatement(sqlCreateWarningManagerDetail);

            // insert stmCreateWarningManageStation
            stmCreateWarningManageStation.setString(1, warningManagerStationDTO.getCode());
            stmCreateWarningManageStation.setString(2, warningManagerStationDTO.getName());
            stmCreateWarningManageStation.setString(3, warningManagerStationDTO.getDescription());
            stmCreateWarningManageStation.setString(4, warningManagerStationDTO.getContent());
            stmCreateWarningManageStation.setString(5, warningManagerStationDTO.getColor());
            stmCreateWarningManageStation.setString(6, warningManagerStationDTO.getIcon());
            stmCreateWarningManageStation.setString(7, warningManagerStationDTO.getStationId());
            stmCreateWarningManageStation.setString(8, warningManagerStationDTO.getCreateBy());
            stmCreateWarningManageStation.executeUpdate();

            // insert stmCreateWarningManagerDetail
            List<WarningManagerDetailDTO> dataWarning = warningManagerStationDTO.getDataWarning();

            for (WarningManagerDetailDTO obj: dataWarning) {
                stmCreateWarningManagerDetail.setLong(1, obj.getWarningThresholdId());
                stmCreateWarningManagerDetail.setString(2, obj.getCreateBy());
                stmCreateWarningManagerDetail.addBatch();

            }
            stmCreateWarningManagerDetail.executeBatch();

            connection.commit();

            stmCreateWarningManageStation.close();
            stmCreateWarningManagerDetail.close();
        } catch (Exception e){
            logger.info("WarningManagerStationDAOImpl Exception : {}",e.getMessage());
            if(e instanceof SQLIntegrityConstraintViolationException){
                return DefaultResponseDTO.builder().status(0).message("Mã cảnh báo đã tồn tại").build();
            }
             return DefaultResponseDTO.builder().status(0).message("Không thành công").build();
        } finally {
            connection.close();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException {
        String sql = "select wd.id, pt.parameter_type_id, wt.id as warning_threshold_id, pt.parameter_type_name, wt.code, wt.level_warning, wt.level_clean, wd.created_by, wd.created_at from warning_manage_detail wd inner join warning_threshold wt on wt.id = wd.warning_threshold_id inner join warning_threshold_value wv on wv.id = wt.warning_threshold_value_id inner join parameter_type pt on pt.parameter_type_id = wv.parameter_type_id where wd.warning_manage_station_id = ?";
        logger.info("sql get WarningThresholdINF : {}", sql);
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, WarningManageStationId);
            ResultSet resultSet = statement.executeQuery();
            List<WarningMangerDetailInfoDTO>  warningMangerDetailInfoDTOs =  new ArrayList<>();
            while (resultSet.next()) {
                WarningMangerDetailInfoDTO warningMangerDetailInfoDTO = WarningMangerDetailInfoDTO.builder()
                       .id(resultSet.getLong("id"))
                        .idParameter(resultSet.getLong("parameter_type_id"))
                        .idWarningThreshold(resultSet.getLong("warning_threshold_id"))
                        .nameParameter(resultSet.getString("parameter_type_name"))
                        .warningThresholdCode(resultSet.getString("code"))
                        .warningThreshold(resultSet.getInt("level_warning"))
                        .warningThresholdCancel(resultSet.getInt("level_clean"))
                        .createBy(resultSet.getString("created_by"))
                        .createAt(resultSet.getString("created_at"))
                        .build();
                warningMangerDetailInfoDTOs.add(warningMangerDetailInfoDTO);

            }
            return warningMangerDetailInfoDTOs;
        }
    }

    @Override
    public DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO, List<WarningManagerDetailDTO> deletes,  List<WarningManagerDetailDTO> creates) throws SQLException {
        String sqlUpdate = "update warning_manage_stations set code = ?, name = ?, description = ? , content = ? , color = ? , icon = ? where id = ?";
        String sqlDelete = "delete from warning_manage_detail where id = ?";
        String sqlCreate = "insert into warning_manage_detail(id, warning_manage_station_id, warning_threshold_id, created_by, created_at) values (WARNING_MANAGER_DETAIL_SEQ.nextval,?,?,?,sysdate)";

        logger.info("WarningManagerStationDAOImpl sql : {}",sqlUpdate);

        logger.info("WarningManagerStationDAOImpl sql : {}",sqlDelete);

        logger.info("WarningManagerStationDAOImpl sql : {}",sqlCreate);

        Connection connection = ds.getConnection();

        try{
            connection.setAutoCommit(false);
            PreparedStatement stmUpdate = connection.prepareStatement(sqlUpdate);
            PreparedStatement stmDelete = connection.prepareStatement(sqlDelete);
            PreparedStatement stmCreate = connection.prepareStatement(sqlCreate);

            // thực hiện update
            stmUpdate.setString(1, warningManagerStationDTO.getCode());
            stmUpdate.setString(2, warningManagerStationDTO.getName());
            stmUpdate.setString(3, warningManagerStationDTO.getDescription());
            stmUpdate.setString(4, warningManagerStationDTO.getContent());
            stmUpdate.setString(5, warningManagerStationDTO.getColor());
            stmUpdate.setString(6, warningManagerStationDTO.getIcon());
            stmUpdate.setLong(7, warningManagerStationDTO.getId());
            stmUpdate.executeUpdate();

            // thực hiện thêm mới

            for (WarningManagerDetailDTO obj: creates) {
                stmCreate.setLong(1, warningManagerStationDTO.getId());
                stmCreate.setLong(2, obj.getWarningThresholdId());
                stmCreate.setString(3, obj.getCreateBy());
                stmCreate.addBatch();

            }
            stmCreate.executeBatch();
            // thực hiện xóa

            for (WarningManagerDetailDTO obj: deletes) {
                stmDelete.setLong(1, obj.getId());
                stmDelete.addBatch();

            }
            stmDelete.executeBatch();
            connection.commit();

             stmUpdate.close();
             stmDelete.close();
             stmCreate.close();

        } catch (Exception e){
            logger.info("WarningManagerStationDAOImpl Exception : {}",e.getMessage());
            if(e instanceof SQLIntegrityConstraintViolationException){
                return DefaultResponseDTO.builder().status(0).message("Mã cảnh báo đã tồn tại").build();
            }
            logger.info("WarningManagerStationDAOImpl exception : {}",e.getMessage());
        } finally {
            connection.close();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public DefaultResponseDTO deleteWarningManagerStation(List<Long> id) throws SQLException {
        String sqlDeleteManager = "delete from warning_manage_stations where id =?";
        String sqlDeleteDetail = "delete from warning_manage_detail where warning_manage_station_id = ?";

        Connection connection = ds.getConnection();
        try{
            connection.setAutoCommit(false);
            PreparedStatement stmDetateManager = connection.prepareStatement(sqlDeleteManager);
            PreparedStatement stmDeleteDetail = connection.prepareStatement(sqlDeleteDetail);

            for (Long tmp: id) {
                stmDeleteDetail.setLong(1,tmp);
                stmDeleteDetail.addBatch();
                stmDetateManager.setLong(1, tmp);
                stmDetateManager.addBatch();
            }
            stmDeleteDetail.executeBatch();
            stmDetateManager.executeBatch();
            connection.commit();

        } catch (Exception e){
            connection.rollback();
            logger.error("WarningManagerStationDAOImpl exception : {}", e.getMessage());
            return DefaultResponseDTO.builder().status(0).message("Không thành công").build();

        } finally {
            connection.close();
        }

        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public List<ComboBoxStr> getWarningComboBox(SelectWarningManagerStrVM selectVM) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select id,code, name from warning_manage_stations where 1 = 1 and station_id = ? ";
            if(selectVM.getTerm()!=null && !selectVM.getTerm().equals("")){
                sql = sql+ " code like ? or name like ?";
            }
            sql = sql + " and rownum < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectVM.getId());
            if(selectVM.getTerm()!=null && !selectVM.getTerm().equals("")){
                statement.setString(2,"%"+selectVM.getTerm()+"%");
                statement.setString(3,"%"+selectVM.getTerm()+"%");
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
}
