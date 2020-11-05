package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;

import java.sql.SQLException;
import java.util.List;

public interface WarningManagerStationDAO {
    List<ComboBox> getListParameterWarningConfig(SelectWarningManagerVM selectVM) throws SQLException;
    List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
}
