package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.ParameterChartMappingDAO;
import com.neo.nbdapi.entity.ParameterChartMapping;
import com.neo.nbdapi.utils.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Repository
public class ParameterChartMappingDAOImpl implements ParameterChartMappingDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public ParameterChartMapping getParameterChartMappingByParameterTypeId(Long parameterTypeId) throws SQLException {
        String sql = "SELECT id, parameter_type_id, template_dir, created_date, created_by FROM parameter_chart_mapping WHERE parameter_type_id = ?";
        ParameterChartMapping parameterChartMapping = null;
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, parameterTypeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                parameterChartMapping = ParameterChartMapping.builder()
                        .id(resultSet.getLong("id"))
                        .parameterTypeId(resultSet.getLong("parameter_type_id"))
                        .templateDir(resultSet.getString("template_dir"))
                        .createdDate(DateUtils.getStringFromDateFormat(resultSet.getDate("created_date"), "dd/MM/yyyy HH:mm"))
                        .createdBy(resultSet.getString("created_by"))
                        .build();
            }
        }
        return parameterChartMapping;
    }
}
