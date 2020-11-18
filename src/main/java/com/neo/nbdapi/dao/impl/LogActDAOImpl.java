package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.LogActDAO;
import com.neo.nbdapi.dto.LogActDTO;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.utils.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Repository
public class LogActDAOImpl implements LogActDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<LogActDTO> getListLogActByObjSearch(SearchLogAct objectSearch) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT la.id AS log_id, mn.id AS menu_id, mn.name AS menu_name, la.act, la.created_by, la.created_at FROM log_act la JOIN menu mn ON la.menu_id = mn.id  WHERE 1 = 1 ");
        List<Object> paramSearch = new ArrayList<>();
        // set param query to sql
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
            sql.append(" AND created_at >= TO_DATE(?, 'dd/mm/yyyy') ");
            paramSearch.add(objectSearch.getFromDate());
        }
        if (Strings.isNotEmpty(objectSearch.getToDate())) {
            sql.append(" AND created_at <= TO_DATE(?, 'dd/mm/yyyy') ");
            paramSearch.add(objectSearch.getToDate());
        }

        sql.append(" ORDER BY created_at DESC");

        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            for(int i = 0; i < paramSearch.size(); i++) {
                statement.setObject(i + 1, paramSearch.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            List<LogActDTO> logActList = new ArrayList<>();

            while (resultSet.next()) {
                LogActDTO logActDTO = LogActDTO
                        .builder()
                        .id(resultSet.getLong("log_id"))
                        .menuId(resultSet.getLong("menu_id"))
                        .menuName(resultSet.getString("menu_name"))
                        .act(resultSet.getString("act"))
                        .createdAt(DateUtils.convertDateToString(resultSet.getDate("created_at")))
                        .createdBy(resultSet.getString("created_by"))
                        .build();
                logActList.add(logActDTO);
            }
            return logActList;
        }
    }
}
