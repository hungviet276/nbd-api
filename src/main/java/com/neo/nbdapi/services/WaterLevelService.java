package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.WaterLevel;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;

import java.sql.SQLException;
import java.util.List;

public interface WaterLevelService {
    DefaultPaginationDTO getListWaterLevel(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    DefaultResponseDTO updateWaterLevel(WaterLevelVM waterLevelVM) throws SQLException;
    List<WaterLevel> getListWaterLevelByTime(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException, BusinessException;
}
