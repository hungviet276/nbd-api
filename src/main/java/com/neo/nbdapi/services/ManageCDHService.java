package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.LogActDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.LogCDH;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.objsearch.SearchCDHHistory;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.SQLException;
import java.util.List;

public interface ManageCDHService {

    DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    List<ComboBoxStr> getListStations(String userId) throws SQLException, BusinessException;

    List<ComboBox> getListParameterByStations(String stationId) throws SQLException, BusinessException;

    List<LogCDH> getListOutpust2(SearchCDHHistory objecCdhHistory) throws SQLException;

    SXSSFWorkbook export(SearchCDHHistory objecCdhHistory) throws SQLException;


}
