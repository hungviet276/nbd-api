package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.UserInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.ChangerPassVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import com.neo.nbdapi.services.ChangerPassService;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.services.objsearch.SearchUsesManager;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Date;
import java.util.*;

@Service
public class ChangerPassServiceImpl implements ChangerPassService {

    private Logger logger = LogManager.getLogger(ChangerPassServiceImpl.class);

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public String ChangerPass(ChangerPassVM changerPassVM) throws SQLException, BusinessException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String old_pass = getOldPass(username);
        System.out.println("old_pass--------------"+old_pass);
        if (old_pass.indexOf("true_") == 0) {
            String old_pass_substr = old_pass.substring(5, old_pass.length());
            System.out.println("matches---------------" + passwordEncoder.matches(changerPassVM.getOld_pass(), old_pass));
            if (passwordEncoder.matches(changerPassVM.getOld_pass(), old_pass_substr)) {
                return update_pass(changerPassVM.getNew_pass());
            } else {
                return "pass_word_sai";
            }
        } else {
            return "error";
        }
    }

    @Override
    public String update_pass(String newspass) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.update_password(?,?) ;end;";
        long startTime = System.nanoTime();
        List<Map<String, String>> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            Long valueId = new Date().getTime();
            ps = conn.prepareCall(proc);
            ps.registerOutParameter(1, OracleTypes.VARCHAR);
            ps.setString(2, passwordEncoder.encode(newspass));
            ps.setString(3, SecurityContextHolder.getContext().getAuthentication().getName());
            ps.execute();
            String result = ps.getString(1);
            return result;
        } catch (Exception e) {
            logger.info("exception {} ExtendDao get list Customer_reg", e);
            return "false";
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("resultSet.close Exception : {}", e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("preparedStatement.close Exception : {}", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("connection.close Exception : {}", e);
                }
            }
        }
    }

    @Override
    public String getOldPass(String userId) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.get_old_password(?) ;end;";
        long startTime = System.nanoTime();
        List<Map<String, String>> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            Long valueId = new Date().getTime();
            ps = conn.prepareCall(proc);
            ps.registerOutParameter(1, OracleTypes.VARCHAR);
            ps.setString(2, userId);
            ps.execute();
            String result = ps.getString(1);
            return result;

        } catch (Exception e) {
            logger.info("exception {} ExtendDao get list Customer_reg", e);
            return "false";
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("resultSet.close Exception : {}", e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("preparedStatement.close Exception : {}", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("connection.close Exception : {}", e);
                }
            }
        }
    }
}

