package com.neo.nbdapi.rest;

import com.neo.nbdapi.entity.ParameterChartMapping;
import com.neo.nbdapi.entity.ParameterChartMappingAndData;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;
import com.neo.nbdapi.services.ReportService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_REPORT)
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/get-parameter-chart-mapping-and-data")
    public ParameterChartMappingAndData getgetParameterChartMappingAndData(@RequestBody GetParameterChartMappingAndDataVM request) {
        return reportService.getgetParameterChartMappingAndData(request);
    }
}
