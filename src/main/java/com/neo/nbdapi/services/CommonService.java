/**
 * 
 */
package com.neo.nbdapi.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class CommonService {
	@Autowired
	private HikariDataSource ds;

	@Autowired
	@Qualifier("objectMapper")
	private ObjectMapper objectMapper;

	public Long getSequence(String sequenceName) throws SQLException, BusinessException {
    	String sql = "select %s.nextval from dual";
    	sql = String.format(sql, sequenceName);
    	Long result = 0L;
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql);) {
            ResultSet rs = st.executeQuery();
            rs.next();
            result = rs.getLong(1);
            rs.close();
            return result;
        }
    }
}
