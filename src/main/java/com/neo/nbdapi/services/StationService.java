package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.SelectStationDTO;
import com.neo.nbdapi.dto.StationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;

import java.sql.SQLException;
import java.util.List;

public interface StationService {
    List<ComboBox> getStationComboBox(String query) throws SQLException;

    List<StationDTO> getAllStation();
}
