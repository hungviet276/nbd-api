package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.exception.BusinessException;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class PaginationDAOImpl implements PaginationDAO {

    private Logger logger = LogManager.getLogger(PaginationDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Override
    public ResultSet getResultPagination(String sql, int pageNumber, int recordPerPage, Object... parameter) throws SQLException {
            if (recordPerPage > 0) {
                try (Connection connection = ds.getConnection()) {
                    StringBuilder sqlPagination = new StringBuilder("");
                    if (pageNumber < 2) {
                        sqlPagination.append("select rownum stt, xlpt.* from (");
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
                    PreparedStatement statement = connection.prepareStatement(sqlPagination.toString());
                    for (int i = 0; i < parameter.length; i++) {
                        statement.setObject(i + 1, parameter[i]);
                    }
                    return statement.executeQuery();
                }
            }
        return null;
    }

    @Override
    public long countResultQuery(String sql, Object... parameter) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            StringBuilder sqlPagination = new StringBuilder("");
            sqlPagination.append("SELECT count(1) FROM (");
            sqlPagination.append(sql);
            sqlPagination.append(")");
            PreparedStatement statement = connection.prepareStatement(sqlPagination.toString());
            for (int i = 0; i < parameter.length; i++) {
                statement.setObject(i + 1, parameter[i]);
            }
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
