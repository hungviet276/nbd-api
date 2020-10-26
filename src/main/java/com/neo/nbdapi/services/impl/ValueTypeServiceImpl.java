package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.ValueTypeDAO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.ValueTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class ValueTypeServiceImpl implements ValueTypeService {

    @Autowired
    private ValueTypeDAO valueTypeDAO;

    @Override
    public List<ComboBox> getValueTypesSelect(SelectVM selectVM) throws SQLException {
        return valueTypeDAO.getValueTypesSelect(selectVM.getTerm());
    }

    @Override
    public List<ComboBox> getValueTypesWithStationSelect(Long stationId) throws SQLException {
        return valueTypeDAO.getValueTypesWithStationSelect(stationId);
    }

    @Override
    public ComboBox getStationValueType(Long stationId, Long valueTypeId) throws SQLException {
        return valueTypeDAO.getStationValueType(stationId, valueTypeId);
    }
}
