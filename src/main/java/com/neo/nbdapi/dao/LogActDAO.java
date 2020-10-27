package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.LogActDTO;
import com.neo.nbdapi.entity.LogAct;
import com.neo.nbdapi.services.objsearch.SearchLogAct;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
public interface LogActDAO {
    List<LogActDTO> getListLogActByObjSearch(SearchLogAct  objectSearch) throws SQLException;
}
