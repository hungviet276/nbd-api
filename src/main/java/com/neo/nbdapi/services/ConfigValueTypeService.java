package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.StationValueTypeSpatialDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

public interface ConfigValueTypeService {
    DefaultPaginationDTO getListConfigValueType(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    List<ComboBox> getValueType(Long stationId) throws SQLException;
    List<ComboBox> getStationComboBox(String query) throws SQLException;
    StationValueTypeSpatialDTO getStationValueTypeSpatial(@RequestParam Long idStation, @RequestParam Long idValueType) throws  SQLException;

}
