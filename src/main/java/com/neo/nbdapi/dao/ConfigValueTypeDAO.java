package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.StationValueTypeSpatialDTO;
import com.neo.nbdapi.entity.ComboBox;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

public interface ConfigValueTypeDAO {
    public List<ComboBox> getValueType(Long stationId) throws SQLException;
    List<ComboBox> getStationComboBox(String query) throws SQLException;
    StationValueTypeSpatialDTO getStationValueTypeSpatial(@RequestParam Long idStation, @RequestParam Long idValueType) throws  SQLException;
}
