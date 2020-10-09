package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.LogActDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.LogAct;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.LogActService;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.services.objsearch.SearchMenu;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */

@Service
public class LogActServiceImpl implements LogActService {

    private Logger logger = LogManager.getLogger(LogActServiceImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private LogActDAO logActDAO;

    @Autowired
    private PaginationDAO paginationDAO;

    /**
     * service get list log action pagination
     * @param defaultRequestPagingVM
     * @return
     */
    @Override
    public DefaultPaginationDTO getListLogActPagination(DefaultRequestPagingVM defaultRequestPagingVM) {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<LogAct> logActList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            // pageNumber = start, recordPerPage = length
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("SELECT id, menu, act, created_by, created_at FROM log_act WHERE 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set param query to sql
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchLogAct objectSearch = objectMapper.readValue(search, SearchLogAct.class);
                    if (Strings.isNotEmpty(objectSearch.getId())) {
                        sql.append(" AND id = ? ");
                        paramSearch.add(objectSearch.getId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getMenuId())) {
                        sql.append(" AND menu_id = ? ");
                        paramSearch.add(objectSearch.getMenuId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getAct())) {
                        sql.append(" AND act = ? ");
                        paramSearch.add(objectSearch.getAct());
                    }
                    if (Strings.isNotEmpty(objectSearch.getCreatedBy())) {
                        sql.append(" AND created_by LIKE ? ");
                        paramSearch.add("%" + objectSearch.getCreatedBy() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                        sql.append(" AND created_at >= TO_DATE(?, 'yyyy/mm/dd') ");
                        paramSearch.add(objectSearch.getFromDate());
                    }
                    if (Strings.isNotEmpty(objectSearch.getToDate())) {
                        sql.append(" AND created_at <= TO_DATE(?, 'yyyy/mm/dd') ");
                        paramSearch.add(objectSearch.getToDate());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sql.append(" ORDER BY created_at DESC");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            // get result query by paging
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                LogAct logAct = LogAct
                        .builder()
                        .id(resultSetListData.getLong("id"))
                        .menuId(resultSetListData.getLong("menu_id"))
                        .act(resultSetListData.getString("act"))
                        .createdBy(resultSetListData.getString("created_by"))
                        .createdAt(resultSetListData.getDate("created_at"))
                        .build();
                logActList.add(logAct);
            }

            // count result, totalElements
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(logActList.size())
                    .recordsTotal(total)
                    .content(logActList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(logActList)
                    .build();
        }
    }
}
