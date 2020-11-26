package com.neo.nbdapi.rest;

import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.StationService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * API lấy danh sách các trạm dựa vào loại trạm (ObjectType)
     * @param objectType
     * @return
     * @throws SQLException
     */
    @GetMapping(value = "/get-all-station-csv", produces = {"text/plain"})
    public String geAllStation(@RequestParam(required = false, name = "type") String objectType) throws SQLException {
         return stationService.getStationWithObjectType(objectType);
    }

    @PostMapping("/station-select-water-level")
    public List<ComboBoxStr> getStationComboBoxWaterLevel(@RequestBody SelectVM selectVM) throws SQLException {
        return stationService.getStationComboBoxWaterLevel(selectVM.getTerm());
    }
}
