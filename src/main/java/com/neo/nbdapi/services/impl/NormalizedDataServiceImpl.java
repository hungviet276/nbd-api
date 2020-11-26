package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.services.NormalizedDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NormalizedDataServiceImpl implements NormalizedDataService {
    private Logger logger = LogManager.getLogger(NormalizedDataServiceImpl.class);

    @Autowired
    private StationTimeSeriesDAO stationTimeSeriesDAO;

    @Autowired
    private StationDAO stationDAO;

    @Override
    public List<ComboBoxStr> getAllStationOwnedByUser() throws SQLException {
        List<Object[]> stationList = stationDAO.getAllStationOwnedByUser();
        return null;
    }

    @Override
    public List<StationTimeSeries> findByStationId(String stationId) throws SQLException {
        return stationTimeSeriesDAO.findByStationId(stationId);
    }
}
