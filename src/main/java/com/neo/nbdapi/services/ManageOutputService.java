package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ManageOutputService {

    DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM,String sqlStatement) throws SQLException, BusinessException;

    List<ComboBox> getListStations(String userId) throws SQLException, BusinessException;

    List<ComboBox> getListParameterByStations(String stationId) throws SQLException, BusinessException;

    String getSqlStatement(String stationId,String parameterTypeId,String fromDate,String toDate) throws SQLException, BusinessException;
}
