package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ParameterChartMapping;

import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public interface ParameterChartMappingService {
    ParameterChartMapping getParameterChartMappingByParameterTypeId(Integer parameterTypeId) throws SQLException;
}
