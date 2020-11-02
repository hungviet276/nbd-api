package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.WarningThresholdDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;
import com.neo.nbdapi.services.WarningThresoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class WarningThresoldServiceImpl implements WarningThresoldService {

    @Autowired
    private WarningThresholdDAO warningThresholdDAO;

    @Override
    public List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException {
        return warningThresholdDAO.getListCodeWarningThreSold(query);
    }

    @Override
    public DefaultResponseDTO getDuplicateCodeWarningThreshold(String code) throws SQLException {
        return warningThresholdDAO.getDuplicateCodeWarningThreshold(code);
    }

    @Override
    public List<WarningThreshold> getWarningThresholds(Long thresholdValueTypeId) throws SQLException {
        return warningThresholdDAO.getWarningThresholds(thresholdValueTypeId);
    }
}
