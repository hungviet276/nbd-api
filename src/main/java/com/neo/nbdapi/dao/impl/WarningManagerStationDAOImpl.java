package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.WarningManagerStationDAO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WarningManagerStationDAOImpl implements WarningManagerStationDAO {
    private Logger logger = LogManager.getLogger(WarningManagerStationDAOImpl.class);
    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerVM selectVM) throws SQLException {
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
            statement.setLong(1, selectVM.getId());
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
            String sql = "select wt.id, wt.code from warning_threshold wt inner join warning_threshold_value wv on wt.warning_threshold_value_id = wv.id where wt.status = 1 and wv.parameter_type_id = ?";
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
}
