package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.services.StationService;
import com.neo.nbdapi.utils.CsvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationDAO stationDAO;

    @Override
    public List<ComboBoxStr> getStationComboBox(String query) throws SQLException {
        return stationDAO.getStationComboBox(query);
    }

    @Override
    public String getAllStationCsv() throws SQLException {
        String header = "stationId,stationCode,stationName,image,longitude,latitude,transMiss,address,areaName,isActive,stationTypeName";
        return CsvUtils.writeToCsvText(stationDAO.getAllStationOwnedByUser(), header);
    }

    @Override
    public List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException {
        return stationDAO.getStationComboBoxWaterLevel(query);
    }
}
