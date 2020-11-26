package com.neo.nbdapi.rest;

import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.services.NormalizedDataService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_DATA)
public class DataController {

    @Autowired
    private NormalizedDataService normalizedData;

    @PostMapping("/get-station-time-by-station")
    public List<StationTimeSeries> getStationByUser(@RequestParam String stationId) throws SQLException {
        return normalizedData.findByStationId(stationId);
    }

}
