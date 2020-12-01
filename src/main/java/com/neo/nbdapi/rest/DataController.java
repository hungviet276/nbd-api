package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.services.NormalizedDataService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/get-value-by-station")
    public List<StationTimeSeriesDTO> getValueOfStationTimeSeries(@RequestBody StationTimeSeriesDTO seriesDTO) {
        return normalizedData.getValueOfStationTimeSeries(seriesDTO);
    }

}
