package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.StationTimeSeries;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface DataService {

    List<StationTimeSeries> findByStationId(String stationId) throws SQLException;

    List<StationTimeSeriesDTO> getValueOfStationTimeSeries(StationTimeSeriesDTO seriesDTO);

    DefaultResponseDTO sendDataToCDH(String stationId, List<StationTimeSeriesDTO>  seriesDTO) throws IOException;
}
