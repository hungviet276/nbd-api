package com.neo.nbdapi.dao;

import com.neo.nbdapi.exception.BusinessException;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PaginationDAO {
	ResultSet getResultPagination(String sql, int pageNumber, int recordPerPage, Object... parameter)
			throws SQLException;

	public long countResultQuery(String sql, Object... parameter) throws SQLException, BusinessException;
}
