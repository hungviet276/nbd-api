package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.GuessDataDTO;
import com.neo.nbdapi.entity.WaterLevel;
import com.neo.nbdapi.entity.WaterLevelExecute;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;

import java.sql.SQLException;
import java.util.List;

public interface WaterLevelDAO {

 List<Object> queryInformation(WaterLevelVM waterLevelVM) throws SQLException;

 DefaultResponseDTO updateWaterLevel(WaterLevelVM waterLevelVM) throws SQLException;

 List<WaterLevel> getListWaterLevelByTime(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException, BusinessException;

 List<WaterLevelExecute> executeWaterLevel(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException;

 DefaultResponseDTO insertTidalPrediction(List<GuessDataDTO> GuessDataDTO, String staionId) throws SQLException;
}
