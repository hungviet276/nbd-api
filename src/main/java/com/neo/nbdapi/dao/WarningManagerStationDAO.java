package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.WarningManagerDetailDTO;
import com.neo.nbdapi.dto.WarningManagerStationDTO;
import com.neo.nbdapi.dto.WarningMangerDetailInfoDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;

import java.sql.SQLException;
import java.util.List;

public interface WarningManagerStationDAO {
    List<ComboBox> getListParameterWarningConfig(SelectWarningManagerVM selectVM) throws SQLException;
    List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
    DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException;
    List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException;
    DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO, List<WarningManagerDetailDTO> deletes, List<WarningManagerDetailDTO> creates) throws SQLException;
    DefaultResponseDTO deleteWarningManagerStation(Long id) throws SQLException;
}
