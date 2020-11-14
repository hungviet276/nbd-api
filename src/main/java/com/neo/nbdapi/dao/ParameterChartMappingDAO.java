package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ParameterChartMapping;

import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public interface ParameterChartMappingDAO {
    ParameterChartMapping getParameterChartMappingByParameterTypeId(Long parameterTypeId) throws SQLException;
}
