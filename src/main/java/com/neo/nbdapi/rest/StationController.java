package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.StationService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_STATION)
public class StationController {
    @Autowired
    private StationService stationService;

    @PostMapping("/station-select")
    public List<ComboBoxStr> getStationComboBox(@RequestBody SelectVM selectVM) throws SQLException {
        return stationService.getStationComboBox(selectVM.getTerm());
    }

    @GetMapping(value = "/get-all-station-csv", produces = {"application/vnd.ms-excel:UTF-8"})
    public String geAllStation(HttpServletResponse httpServletResponse) throws SQLException {
        httpServletResponse.setContentType("application/vnd.ms-excel:UTF-8"); // or you can use text/csv
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=all_station.csv");
         return writeToStringCsv(stationService.getAllStation());
    }

    private String writeToStringCsv(List<StationMapDTO> stationMapDTOList) {
        StringBuilder output = new StringBuilder();
        // append header to csv data
        output.append("latitude,longtitude,stationId,stationCode,stationName,elevation,image,transMiss,address,areaName,isActive,stationTypeName").append("\n");
        stationMapDTOList.forEach(stationMapDTO -> {
            System.out.println(stationMapDTO);
            output.append(stationMapDTO.getLatitude())
                    .append(",")
                    .append(stationMapDTO.getLongtitude())
                    .append(",")
                    .append(stationMapDTO.getStationId())
                    .append(",")
                    .append(stationMapDTO.getStationCode())
                    .append(",")
                    .append(stationMapDTO.getStationName())
                    .append(",")
                    .append(stationMapDTO.getElevation())
                    .append(",")
                    .append(stationMapDTO.getImage())
                    .append(",")
                    .append(stationMapDTO.getTransMiss())
                    .append(",")
                    .append(stationMapDTO.getAddress())
                    .append(",")
                    .append(stationMapDTO.getAreaName())
                    .append(",")
                    .append(stationMapDTO.getIsActive())
                    .append(",")
                    .append(stationMapDTO.getStationTypeName())
                    .append("\n");
        });
        return output.toString();
    }
}
