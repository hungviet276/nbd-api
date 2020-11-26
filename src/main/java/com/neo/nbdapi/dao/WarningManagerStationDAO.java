package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerStrVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.List;

public interface WarningManagerStationDAO {
    List<ComboBox> getListParameterWarningConfig(SelectWarningManagerStrVM selectVM) throws SQLException;
    List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
    DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException;
    DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO, List<WarningManagerDetailDTO> deletes, List<WarningManagerDetailDTO> creates) throws SQLException;
    DefaultResponseDTO deleteWarningManagerStation(List<Long> id) throws SQLException;
    List<ComboBoxStr> getWarningComboBox( SelectWarningManagerStrVM selectVM) throws SQLException;

    // thanglv
    List<NotificationToDayDTO> getListWarningManagerStationByDate(String startDate, String endDate) throws SQLException;
}
