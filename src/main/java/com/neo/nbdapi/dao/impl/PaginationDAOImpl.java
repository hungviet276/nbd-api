package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.PaginationDAO;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaginationDAOImpl implements PaginationDAO {

    private Logger logger = LogManager.getLogger(PaginationDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    /**
     * Get result paging
     * @param sql
     * @param pageNumber
     * @param recordPerPage
     * @param parameter
     * @return
     * @throws SQLException
     */
    @Override
    public ResultSet getResultPagination(Connection connection, String sql, int pageNumber, int recordPerPage, List<Object> parameter) throws SQLException {
        StringBuilder sqlPagination = new StringBuilder("");
        if (pageNumber < 2) {
            sqlPagination.append("select rownum row_stt, xlpt.* from (");
            sqlPagination.append(sql);
            sqlPagination.append(") xlpt where rownum < ");
            sqlPagination.append(recordPerPage + 1);
        } else {
            sqlPagination.append("select /*+ first_rows(");
            sqlPagination.append(recordPerPage);
            sqlPagination.append(") */ xlpt.* from (select rownum row_stt, xlpt.* from (");
            sqlPagination.append(sql);
            sqlPagination.append(") xlpt where rownum <= ");
            sqlPagination.append(pageNumber * recordPerPage);
            sqlPagination.append(" ) xlpt where row_stt > ");
            sqlPagination.append(((pageNumber - 1) * recordPerPage));

        }
        logger.debug("JDBC execute query: {}", sqlPagination);
        PreparedStatement statement = connection.prepareStatement(sqlPagination.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < parameter.size(); i++) {
            statement.setObject(i + 1, parameter.get(i));
        }
        return statement.executeQuery();
    }

    /**
     * count all result query of pagination
     * @param sql
     * @param parameter
     * @return
     * @throws SQLException
     */
    @Override
    public long countResultQuery(String sql, List<Object> parameter) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            StringBuilder sqlPagination = new StringBuilder("");
            sqlPagination.append("SELECT count(1) FROM (");
            sqlPagination.append(sql);
            sqlPagination.append(")");
            PreparedStatement statement = connection.prepareStatement(sqlPagination.toString());
            for (int i = 0; i < parameter.size(); i++) {
                statement.setObject(i + 1, parameter.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
