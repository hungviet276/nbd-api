package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

public interface ValueTypeService {
    List<ComboBox> getValueTypesSelect(@RequestBody SelectVM selectVM) throws SQLException;
    List<ComboBox> getValueTypesWithStationSelect(@RequestParam String stationId) throws SQLException;
    public ComboBox getStationValueType(Long stationId, Long valueTypeId) throws SQLException;
}
