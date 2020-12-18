package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.HistoryOutPutsDTO;
import com.neo.nbdapi.dto.UserGroupDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.ManageOutPutVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ManageOutputService {

    DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    List<ComboBoxStr> getListStations(String userId,String stationsType_search) throws SQLException, BusinessException;

    List<ComboBoxStr> getListStationsType() throws SQLException, BusinessException;

    List<ComboBox> getListParameterByStations(String stationId) throws SQLException, BusinessException;

    String getSqlStatement(String stationId,String parameterTypeId,String fromDate,String toDate) throws SQLException, BusinessException;

    String editValueProd(ManageOutPutVM manageOutPutVM) throws SQLException, BusinessException;

    List<ComboBoxStr> getListtimeHistory(String prodId,String prodTableName) throws SQLException, BusinessException;

    List<HistoryOutPutsDTO> getHistoryByTimes(String time, String prodId,String tablePrName) throws SQLException, BusinessException;
}
