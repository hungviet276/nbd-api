package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;

import java.sql.SQLException;
import java.util.List;

public interface WarningMangerStationService {
    DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerVM selectVM) throws SQLException;
    public List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException;
    public WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException;
}
