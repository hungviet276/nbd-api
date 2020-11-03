package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;

import java.sql.SQLException;
import java.util.List;

public interface WarningThresoldService {
    List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException;
    public DefaultResponseDTO getDuplicateCodeWarningThreshold(String code) throws SQLException;
    public List<WarningThreshold> getWarningThresholds(Long thresholdValueTypeId) throws SQLException;
}
