package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.*;

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

    // thanglv
    // function get list notitication today
    List<NotificationToDayDTO> getListNotificationToday() throws SQLException;
    NotificationToDayDTO getNotificationById(Long warningManagerStationId) throws SQLException;

    // thanglv
    // function get list warning station history
    DefaultPaginationDTO getWarningStationHistory(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException;
}
