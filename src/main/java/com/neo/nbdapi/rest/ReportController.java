package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.ParameterChartMappingAndDataDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;
import com.neo.nbdapi.rest.vm.GetStationDataReportVM;
import com.neo.nbdapi.services.ReportService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_REPORT)
public class ReportController {

    @Autowired
    private ReportService reportService;

    // lay du lieu report cua 1 yeu to cua tram do theo startdate va enddate
    @PostMapping("/get-parameter-chart-mapping-and-data")
    public ParameterChartMappingAndDataDTO getParameterChartMappingAndData(@RequestBody @Valid GetParameterChartMappingAndDataVM request) throws SQLException, BusinessException {
        System.out.println(request.getType() == null ? "ssss": ">" + request.getType() + "<");
        return reportService.getParameterChartMappingAndData(request);
    }

    // lay ra list parameter type id hien thi cua tram
    @PostMapping("/get-parameter-display-chart")
    public String[] getListParameterDisplayChartOfStation(@RequestBody @Valid GetStationDataReportVM request) throws SQLException, BusinessException {
        return  reportService.getListParameterDisplayChartOfStation(request);
    }

}
