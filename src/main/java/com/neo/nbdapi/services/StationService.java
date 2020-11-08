package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;

import java.sql.SQLException;
import java.util.List;

public interface StationService {
    List<ComboBox> getStationComboBox(String query) throws SQLException;

    List<StationMapDTO> getAllStation() throws SQLException;
}
