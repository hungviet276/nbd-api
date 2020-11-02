package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.WarningThresholdDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;
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
public class WarningThresholdDAOImpl implements WarningThresholdDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select id, code from warning_threshold where 1=1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and code like ?";
            }
            sql += " and ROWNUM < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,query);
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
    public DefaultResponseDTO getDuplicateCodeWarningThreshold(String code) throws SQLException {
        String sql = "select code from warning_threshold where code = ?";
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return DefaultResponseDTO.builder().status(1).message("Mã cảnh báo đã tồn tại").build();
            } else{
                return DefaultResponseDTO.builder().status(0).message("Mã cảnh báo chưa tồn tại").build();
            }
        }
    }

    @Override
    public List<WarningThreshold> getWarningThresholds(Long thresholdValueTypeId) throws SQLException {
        List<WarningThreshold> warningThresholds = new ArrayList<>();
        String sql = "select wt.code, pt.parameter_type_code,wv.parameter_type_id, pt.parameter_type_name, wt.level_warning, wt.level_clean, wt.status from warning_threshold wt inner join warning_threshold_value wv on wt.warning_threshold_value_id = wv.id inner join parameter_type pt on pt.parameter_type_id = wv.parameter_type_id where wv.id = ?";
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, thresholdValueTypeId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                    WarningThreshold warningThreshold =
                            WarningThreshold.builder().
                                    warningThresholdCode(resultSet.getString("code"))
                                    .idParameter(resultSet.getLong("parameter_type_id"))
                                    .nameParameter(resultSet.getString("parameter_type_code")+"-"+resultSet.getString("parameter_type_name"))
                                    .thresholdId(resultSet.getLong("level_warning"))
                                    .thresholdCancelID(resultSet.getLong("level_clean"))
                                    .status(resultSet.getInt("status"))
                                    .build();
                warningThresholds.add(warningThreshold);
            }
        }
        return warningThresholds;
    }
}
