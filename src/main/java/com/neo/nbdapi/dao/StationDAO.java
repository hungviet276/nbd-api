package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;

import java.sql.SQLException;
import java.util.List;

public interface StationDAO {
    List<ComboBox> getStationComboBox(String query) throws SQLException;
}
