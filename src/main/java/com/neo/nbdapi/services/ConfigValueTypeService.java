package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.*;
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
    List<ComboBox> getStationComboBox(String query, Long idStation)  throws SQLException;
    StationValueTypeSpatialDTO getStationValueTypeSpatial(Long idStation, Long idValueType, String code) throws  SQLException;
    DefaultResponseDTO createConfigValuetype(ConfigValueTypeDTO configValueTypeDTO) throws Exception;
    List<StationValueTypeSpatialDTO> getStationValueTypeSpatials(Long idConfigValueTypeParent) throws SQLException;
    DefaultResponseDTO editConfigValuetype(ConfigValueTypeDTO configValueTypeDTO) throws Exception;
    DefaultResponseDTO deleteConfigValuetype(Long id) throws Exception;

}
