package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
public interface LogActService {
    DefaultPaginationDTO getListLogActPagination(DefaultRequestPagingVM defaultRequestPagingVM);

    List<MenuDTO> getListMenuViewLogOfUser() throws SQLException;

    SXSSFWorkbook export(SearchLogAct searchLogAct) throws SQLException;
}
