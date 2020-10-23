package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.WarningThresoldDAO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.services.WarningThresoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class WarningThresoldServiceImpl implements WarningThresoldService {

    @Autowired
    private WarningThresoldDAO warningThresoldDAO;

    @Override
    public List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException {
        return warningThresoldDAO.getListCodeWarningThreSold(query);
    }
}
