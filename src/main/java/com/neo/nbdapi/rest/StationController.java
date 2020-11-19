package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.StationService;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.CsvUtils;
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

    @GetMapping(value = "/get-all-station-csv", produces = {"text/plain"})
    public String geAllStation() throws SQLException {
         return stationService.getAllStationCsv();
    }

    @PostMapping("/station-select-water-level")
    public List<ComboBoxStr> getStationComboBoxWaterLevel(@RequestBody SelectVM selectVM) throws SQLException {
        return stationService.getStationComboBox(selectVM.getTerm());
    }
}
