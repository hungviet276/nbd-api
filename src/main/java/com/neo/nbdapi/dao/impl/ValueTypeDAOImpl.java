package com.neo.nbdapi.dao.impl;
import com.neo.nbdapi.dao.ValueTypeDAO;
import com.neo.nbdapi.entity.ComboBox;
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
public class ValueTypeDAOImpl implements ValueTypeDAO {

    private Logger logger = LogManager.getLogger(ValueTypeDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getValueTypesSelect(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select parameter_type_id as id, parameter_type_code as code, parameter_type_name as name from parameter_type where 1=1";
            if (query != null && !query.equals("")) {
                sql = sql + " and parameter_type_name like ?";
            }
            sql = sql + " and rownum < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            if (query != null && !query.equals("")) {
                statement.setString(1, "%" + query + "%");
            }
            ResultSet resultSet = statement.executeQuery();
            List<ComboBox> comboBoxes = new ArrayList<>();
            logger.info("ValueTypeDAOImpl query : {}", query);
            while (resultSet.next()) {
                ComboBox comboBox = ComboBox.builder().id(resultSet.getLong(1)).text(resultSet.getString(2) + "-" + resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }

    @Override
    public List<ComboBox> getValueTypesWithStationSelect(String stationId) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select v.parameter_type_id, v.parameter_type_code, v.parameter_type_name from parameter_type v inner join parameter s on v.parameter_type_id = s.parameter_type_id where s.station_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, stationId);
            ResultSet resultSet = statement.executeQuery();
            List<ComboBox> comboBoxes = new ArrayList<>();
            logger.info("ValueTypeDAOImpl query : {}", sql);
            while (resultSet.next()) {
                ComboBox comboBox = ComboBox.builder().id(resultSet.getLong(1)).text(resultSet.getString(2) + "-" + resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }

    @Override
    public ComboBox getStationValueType(String stationId, Long valueTypeId) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select pt.parameter_type_id, pt.parameter_type_code, pt.parameter_type_name from parameter_type pt inner join  parameter p on p.parameter_type_id = pt.parameter_type_id where p.station_id = ? and pt.parameter_type_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, stationId);
            statement.setLong(2, valueTypeId.longValue());
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            logger.info("ValueTypeDAOImpl query : {}", sql);
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong(1)).text(resultSet.getString(2) + "-" + resultSet.getString(3)).build();
            }
            statement.close();
            return comboBox;
        }
    }
}
