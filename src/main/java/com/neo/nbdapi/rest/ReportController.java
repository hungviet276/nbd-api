package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.ParameterChartMappingAndDataDTO;
import com.neo.nbdapi.dto.StationDataReportDTO;
import com.neo.nbdapi.dto.TimeSeriesDataDTO;
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
import java.util.List;

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
        return reportService.getParameterChartMappingAndData(request);
    }

    // lay du lieu report tat ca cac yeu to cua tram do cua 7 ngay truoc tinh tu ngay hom nay
    @PostMapping("/get-station-report-data-seven-day-ago")
    public List<TimeSeriesDataDTO> getStationDataReport(@RequestBody @Valid GetStationDataReportVM request) throws SQLException, BusinessException {
        return  reportService.getStationDataReport(request);
    }

}
