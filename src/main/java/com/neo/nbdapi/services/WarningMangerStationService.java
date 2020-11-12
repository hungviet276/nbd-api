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
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerStrVM selectVM) throws SQLException;
    public List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    public WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
    public DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    public List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException;
    public DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    public DefaultResponseDTO deleteWarningManagerStation(List<Long> id) throws SQLException;
    public List<ComboBoxStr> getWarningComboBox(SelectVM selectVM) throws SQLException ;
}
