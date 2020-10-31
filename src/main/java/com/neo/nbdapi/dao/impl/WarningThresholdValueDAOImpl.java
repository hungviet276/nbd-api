package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.WarningThresholdValueDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.WarningThreshold;
import com.neo.nbdapi.rest.vm.WarningThresholdVM;
import com.neo.nbdapi.rest.vm.WarningThresholdValueVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WarningThresholdValueDAOImpl implements WarningThresholdValueDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public DefaultResponseDTO createWarningThreshold(WarningThresholdValueVM warningThresholdValueVM) throws SQLException {
        Connection connection = ds.getConnection();
        String sqlInsertThresholdValue = "insert into warning_threshold_value (id, parameter_type_id, value_level1, value_level2, value_level3, value_level4, value_level5,station_id) values (WARNING_THRESHOLD_VALUE_SEQ.nextval,?,?,?,?,?,?,?)";
        String sqlInsertThreshold = "insert into warning_threshold(id, code, warning_threshold_value_id, level_warning, LEVEL_CLEAN, status) values (WARNING_THRESHOLD_SEQ.nextval, ?, WARNING_THRESHOLD_VALUE_SEQ.CURRVAL, ?,?,?)";

        try{
            connection.setAutoCommit(false);
            PreparedStatement stmInsertThresholdValue = connection.prepareStatement(sqlInsertThresholdValue);
            PreparedStatement stmInsertThreshold = connection.prepareStatement(sqlInsertThreshold);

            //insert trong config threshol value
            stmInsertThresholdValue.setLong(1,warningThresholdValueVM.getParameterStation());
            stmInsertThresholdValue.setFloat(2,warningThresholdValueVM.getThreshold1());
            stmInsertThresholdValue.setFloat(3,warningThresholdValueVM.getThreshold2());
            stmInsertThresholdValue.setFloat(4,warningThresholdValueVM.getThreshold3());
            stmInsertThresholdValue.setFloat(5,warningThresholdValueVM.getThreshold4());
            stmInsertThresholdValue.setFloat(6,warningThresholdValueVM.getThreshold5());
            stmInsertThresholdValue.setLong(7,warningThresholdValueVM.getStationId());
            stmInsertThresholdValue.executeUpdate();

            // insert các bản ghi warning threshold

            List<WarningThresholdVM> warningThresholdVM = warningThresholdValueVM.getDataThreshold();
            for(WarningThresholdVM tmp : warningThresholdVM){
                stmInsertThreshold.setString(1,tmp.getWarningThresholdCode());
                stmInsertThreshold.setLong(2,tmp.getThresholdId());
                stmInsertThreshold.setLong(3,tmp.getThresholdCancelID());
                stmInsertThreshold.setInt(4,tmp.getStatus());
                stmInsertThreshold.addBatch();
            }
            stmInsertThreshold.executeBatch();
            connection.commit();
        } catch (Exception e){
            connection.rollback();
            throw e;
        } finally {
            if(connection!=null){
                connection.close();
            }
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public DefaultResponseDTO editWarningThreshold(WarningThresholdValueVM warningThresholdValueVM, List<WarningThreshold> deletes, List<WarningThreshold> updates, List<WarningThreshold> creates) throws SQLException {
        String sqlUpdateThresholdValue = "UPDATE warning_threshold_value set value_level1 = ?, value_level2=?, value_level3 = ?, value_level4 = ?, value_level5 = ? where id = ?";
        String sqlDeleteWarningThreshold = "delete from warning_threshold where code = ?";
        String sqlUpdateWarningThreshold = "update warning_threshold set level_clean = ? , level_warning = ?, status = ? where code = ?";
        String sqlCreateWarningThreshold = "insert into warning_threshold(id, code, warning_threshold_value_id, level_warning, LEVEL_CLEAN, status) values (WARNING_THRESHOLD_SEQ.nextval, ?, ?, ?, ?, ?)";
        //chú câu truy vấn bên trên là truyền vào chứ không có để lấy
        Connection connection = ds.getConnection();

        try{
            connection.setAutoCommit(false);
            PreparedStatement stmUpdateThresholdValue = connection.prepareStatement(sqlUpdateThresholdValue);
            PreparedStatement stmDeleteWarningThreshold = connection.prepareStatement(sqlDeleteWarningThreshold);
            PreparedStatement stmUpdateWarningThreshold = connection.prepareStatement(sqlUpdateWarningThreshold);
            PreparedStatement stmCreateWarningThreshold = connection.prepareStatement(sqlCreateWarningThreshold);

            stmUpdateThresholdValue.setFloat(1, warningThresholdValueVM.getThreshold1());
            stmUpdateThresholdValue.setFloat(2, warningThresholdValueVM.getThreshold2());
            stmUpdateThresholdValue.setFloat(3, warningThresholdValueVM.getThreshold3());
            stmUpdateThresholdValue.setFloat(4, warningThresholdValueVM.getThreshold4());
            stmUpdateThresholdValue.setFloat(5, warningThresholdValueVM.getThreshold5());
            stmUpdateThresholdValue.setLong(6, warningThresholdValueVM.getId());
            stmUpdateThresholdValue.executeUpdate();

            // thực hiện delete trước sau đó tạo mới, rồi sau đó update

            for (WarningThreshold  delete : deletes) {
                stmDeleteWarningThreshold.setString(1,delete.getWarningThresholdCode());
                stmDeleteWarningThreshold.addBatch();
            }
            stmDeleteWarningThreshold.executeBatch();

            // thực hiện thêm mới
            for (WarningThreshold create: creates) {
                stmCreateWarningThreshold.setString(1, create.getWarningThresholdCode());
                stmCreateWarningThreshold.setLong(2, warningThresholdValueVM.getId());
                stmCreateWarningThreshold.setLong(3,create.getThresholdId());
                stmCreateWarningThreshold.setLong(4,create.getThresholdCancelID());
                stmCreateWarningThreshold.setInt(5,create.getStatus());
                stmCreateWarningThreshold.addBatch();
            }
            stmCreateWarningThreshold.executeBatch();

            // thực hiện upadte warning threshold

            for(WarningThreshold update : updates){
                stmUpdateWarningThreshold.setLong(1,update.getThresholdCancelID());
                stmUpdateWarningThreshold.setLong(2,update.getThresholdId());
                stmUpdateWarningThreshold.setInt(3,update.getStatus());
                stmUpdateWarningThreshold.setString(4, update.getWarningThresholdCode());
                stmUpdateWarningThreshold.addBatch();
            }
            stmUpdateWarningThreshold.executeBatch();
            connection.commit();
        } catch (Exception e){
            connection.rollback();
            throw e;
        }

        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

}
