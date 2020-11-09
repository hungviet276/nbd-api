package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

public interface ValueTypeDAO {
    List<ComboBox> getValueTypesSelect(String query) throws SQLException;
    List<ComboBox> getValueTypesWithStationSelect(String stationId) throws SQLException;
    ComboBox getStationValueType(Long stationId, Long valueTypeId) throws SQLException;
}
