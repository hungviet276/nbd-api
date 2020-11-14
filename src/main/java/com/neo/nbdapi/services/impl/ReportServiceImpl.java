package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.entity.ParameterChartMappingAndData;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;
import com.neo.nbdapi.services.ReportService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private HikariDataSource ds;

    @Override
    public ParameterChartMappingAndData getgetParameterChartMappingAndData(GetParameterChartMappingAndDataVM request) {
        // validate
        validateReport(request);
        return null;
    }

    private void validateReport(GetParameterChartMappingAndDataVM request) {

    }
}
