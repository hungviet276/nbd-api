package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ParameterChartMappingAndData;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;

import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public interface ReportService {
    ParameterChartMappingAndData getParameterChartMappingAndData(GetParameterChartMappingAndDataVM request) throws SQLException, BusinessException;
}
