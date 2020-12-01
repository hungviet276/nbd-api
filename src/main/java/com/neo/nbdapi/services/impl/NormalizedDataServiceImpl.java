package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.services.NormalizedDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NormalizedDataServiceImpl implements NormalizedDataService {
    private Logger logger = LogManager.getLogger(NormalizedDataServiceImpl.class);

    @Autowired
    private StationTimeSeriesDAO stationTimeSeriesDAO;

    @Override
    public List<StationTimeSeries> findByStationId(String stationId) {
        return stationTimeSeriesDAO.findByStationId(stationId);
    }

    @Override
    public List<StationTimeSeriesDTO> getValueOfStationTimeSeries(StationTimeSeriesDTO seriesDTO) {
        return stationTimeSeriesDAO.getValueOfStationTimeSeries(seriesDTO);
    }
}
