package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;
import com.neo.nbdapi.rest.vm.WarningThresholdValueVM;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.List;

public interface WarningThresholdValueDAO {
    DefaultResponseDTO createWarningThreshold(WarningThresholdValueVM warningThresholdValueVM) throws SQLException;
    DefaultResponseDTO editWarningThreshold(WarningThresholdValueVM warningThresholdValueVM, List<WarningThreshold> deletes, List<WarningThreshold> updates, List<WarningThreshold> creates) throws SQLException;
    ComboBox getValueType(Long id) throws SQLException;
    DefaultResponseDTO deleteWarningThresholdValue(Long id) throws SQLException;

}
