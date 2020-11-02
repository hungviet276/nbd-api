package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;
import com.neo.nbdapi.rest.vm.WarningThresholdVM;

import java.sql.SQLException;
import java.util.List;

public interface WarningThresholdDAO {
    List<ComboBox> getListCodeWarningThreSold(String Query) throws SQLException;
    DefaultResponseDTO getDuplicateCodeWarningThreshold(String code) throws SQLException;
    List<WarningThreshold>  getWarningThresholds(Long thresholdValueTypeId) throws SQLException;
}
