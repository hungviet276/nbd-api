package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerStrVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;

import java.sql.SQLException;
import java.util.List;

public interface WarningMangerStationService {
    DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    List<ComboBox> getListParameterWarningConfig(SelectWarningManagerStrVM selectVM) throws SQLException;
    List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
    DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException;
    DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    DefaultResponseDTO deleteWarningManagerStation(List<Long> id) throws SQLException;
    List<ComboBoxStr> getWarningComboBox(SelectWarningManagerStrVM selectVM) throws SQLException ;
}
