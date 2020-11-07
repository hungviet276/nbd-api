package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationDAO stationDAO;

    @Override
    public List<ComboBox> getStationComboBox(String query) throws SQLException {
        return stationDAO.getStationComboBox(query);
    }

    @Override
    public List<StationMapDTO> getAllStation() throws SQLException {
        return stationDAO.getAllStationMap();
    }
}
