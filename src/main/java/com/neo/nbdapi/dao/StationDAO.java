package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.rest.vm.SelectVM;

import java.sql.SQLException;
import java.util.List;

public interface StationDAO {

    List<ComboBoxStr> getStationComboBox(String query) throws SQLException;

    List<StationMapDTO> getAllStationMap() throws SQLException;

    List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException;
}
