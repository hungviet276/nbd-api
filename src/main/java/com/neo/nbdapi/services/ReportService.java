package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.ParameterChartMappingAndDataDTO;
import com.neo.nbdapi.dto.TimeSeriesDataDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;
import com.neo.nbdapi.rest.vm.GetStationDataReportVM;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public interface ReportService {
    ParameterChartMappingAndDataDTO getParameterChartMappingAndData(GetParameterChartMappingAndDataVM request) throws SQLException, BusinessException;

    List<TimeSeriesDataDTO> getStationDataReport(GetStationDataReportVM request) throws BusinessException, SQLException;
}
