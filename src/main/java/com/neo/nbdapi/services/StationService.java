package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;

import java.sql.SQLException;
import java.util.List;

public interface StationService {
    List<ComboBoxStr> getStationComboBox(String query) throws SQLException;
    List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException;

    String getAllStationCsv() throws SQLException;

    String getStationWithObjectType(String objectType) throws SQLException;
}
