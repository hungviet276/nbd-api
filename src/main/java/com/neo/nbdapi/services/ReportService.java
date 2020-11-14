package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ParameterChartMappingAndData;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public interface ReportService {
    ParameterChartMappingAndData getgetParameterChartMappingAndData(GetParameterChartMappingAndDataVM request);
}
