package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.SQLException;
import java.util.List;

public interface ManageCDHService {

    DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    List<ComboBox> getListStations(String userId) throws SQLException, BusinessException;

    List<ComboBox> getListParameterByStations(String stationId) throws SQLException, BusinessException;

//    SXSSFWorkbook export(SearchLogAct searchLogAct) throws SQLException;
}
