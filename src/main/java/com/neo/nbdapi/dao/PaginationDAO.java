package com.neo.nbdapi.dao;

import com.neo.nbdapi.exception.BusinessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface PaginationDAO {

    ResultSet getResultPagination(Connection connection, String sql, int pageNumber, int recordPerPage, Object... parameter) throws SQLException;
    public long countResultQuery(String sql, Object... parameter) throws SQLException, BusinessException;
}
