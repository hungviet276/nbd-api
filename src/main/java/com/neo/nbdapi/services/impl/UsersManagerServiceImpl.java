package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.entity.UserInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.services.objsearch.SearchMailConfig;
import com.neo.nbdapi.services.objsearch.SearchUsesManager;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Service
public class UsersManagerServiceImpl implements UsersManagerService {

    private Logger logger = LogManager.getLogger(UsersManagerServiceImpl.class);

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private UsersManagerDAO usersManagerDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;


    @Override
    public DefaultPaginationDTO getListUsersPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
            logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
            List<UserInfo> userInfoList = new ArrayList<>();
            try (Connection connection = ds.getConnection()) {
                int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
                int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
                String search = defaultRequestPagingVM.getSearch();

                StringBuilder sql = new StringBuilder("SELECT id,password,name,mobile,position,email,gender,status_id,TO_CHAR(created_date,'dd/mm/yyyy') createdDate,check_role,card_number,code,office_code,date_role  FROM user_info  WHERE 1 = 1 ");
                List<Object> paramSearch = new ArrayList<>();
                logger.debug("Object search: {}", search);
                if (Strings.isNotEmpty(search)) {
                    try {
                        SearchUsesManager objectSearch = objectMapper.readValue(search, SearchUsesManager.class);
                        if (Strings.isNotEmpty(objectSearch.getCode())) {
                            sql.append(" AND code like ? ");
                            paramSearch.add("%" +objectSearch.getCode()+ "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getId())) {
                            sql.append(" AND id LIKE ? ");
                            paramSearch.add("%" + objectSearch.getId() + "%");
                        }
                        if (objectSearch.getFromDate() != null) {
                            sql.append(" AND create_date = ? ");
                            paramSearch.add(objectSearch.getFromDate());
                        }
                        if (objectSearch.getToDate() != null) {
                            sql.append(" AND create_date = ? ");
                            paramSearch.add(objectSearch.getToDate());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
                ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

                while (resultSetListData.next()) {
                    UserInfo userInfo = UserInfo.builder()
                            .id(resultSetListData.getString("id"))
                            .password(resultSetListData.getString("password"))
                            .name(resultSetListData.getString("name"))
                            .mobile(resultSetListData.getString("mobile"))
                            .position(resultSetListData.getString("position"))
                            .email(resultSetListData.getString("email"))
                            .gender(resultSetListData.getInt("gender"))
                            .statusId(resultSetListData.getInt("status_id"))
                            .checkRole(resultSetListData.getInt("check_role"))
                            .code(resultSetListData.getString("code"))
                            .officeCode(resultSetListData.getString("office_code"))
                            .createdDates(resultSetListData.getString("createdDate"))
                            .cardNumber(resultSetListData.getInt("card_number"))
                            .dateRole(resultSetListData.getInt("date_role"))
                            .build();
                    userInfoList.add(userInfo);
                }
                logger.debug("userInfoList", userInfoList);
                // count result
                long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
                return DefaultPaginationDTO
                        .builder()
                        .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                        .recordsFiltered(userInfoList.size())
                        .recordsTotal(total)
                        .content(userInfoList)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                return DefaultPaginationDTO
                        .builder()
                        .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                        .recordsFiltered(0)
                        .recordsTotal(0)
                        .content(userInfoList)
                        .build();
            }
        }

    @Override
    public List<Map<String, String>> getHeaderTacvu(String userId) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.get_act_basic() ;end;";
        long startTime = System.nanoTime();
        List<Map<String, String>> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            Long valueId = new Date().getTime();
            ps = conn.prepareCall(proc);
            ps.registerOutParameter(1, OracleTypes.CURSOR);
            ps.execute();
            rs = (ResultSet) ps.getObject(1);
            ResultSetMetaData metaData = rs.getMetaData();
            int rowCount = metaData.getColumnCount();
            List<String> colNames = new ArrayList<>();
            for (int i = 1; i <= rowCount; i++) {
                colNames.add(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= rowCount; i++) {
                    if (rs.getObject(i) == null) {
                        map.put(colNames.get(i - 1).toUpperCase(), "");
                    } else
                        map.put(colNames.get(i - 1).toUpperCase(), rs.getObject(i).toString().toUpperCase());
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            logger.info("excption {} ExtendDao get list Customer_reg", e);
            return null;
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
    public List<Map<String, String>> getMenu_checked(String id_nhomquyen, String user_id, String thread_id) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.check_enable_chek(?,?,?) ;end;";
        long startTime = System.nanoTime();
        List<Map<String, String>> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            Long valueId = new Date().getTime();
            ps = conn.prepareCall(proc);
            ps.registerOutParameter(1, OracleTypes.CURSOR);
            ps.setString(2, id_nhomquyen);
            ps.setString(3, user_id);
            ps.setString(4, thread_id);
            ps.execute();
            rs = (ResultSet) ps.getObject(1);
            ResultSetMetaData metaData = rs.getMetaData();
            int rowCount = metaData.getColumnCount();
            List<String> colNames = new ArrayList<>();
            for (int i = 1; i <= rowCount; i++) {
                colNames.add(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= rowCount; i++) {
                    if (rs.getObject(i) == null) {
                        map.put(colNames.get(i - 1).toUpperCase(), "");
                    } else
                        map.put(colNames.get(i - 1).toUpperCase(), rs.getObject(i).toString().toUpperCase());
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            logger.info("exception {} ExtendDao get list Customer_reg", e);
            return null;
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
    public List<Map<String, String>> get_role(String user_id, String thread_id) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.get_role(?,?) ;end;";
        long startTime = System.nanoTime();
        List<Map<String, String>> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            Long valueId = new Date().getTime();
            ps = conn.prepareCall(proc);
            ps.registerOutParameter(1, OracleTypes.CURSOR);
            ps.setString(2, user_id);
            ps.setString(3, thread_id);
            ps.execute();
            rs = (ResultSet) ps.getObject(1);
            ResultSetMetaData metaData = rs.getMetaData();
            int rowCount = metaData.getColumnCount();
            List<String> colNames = new ArrayList<>();
            for (int i = 1; i <= rowCount; i++) {
                colNames.add(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= rowCount; i++) {
                    if (rs.getObject(i) == null) {
                        map.put(colNames.get(i - 1).toUpperCase(), "");
                    } else
                        map.put(colNames.get(i - 1).toUpperCase(), rs.getObject(i).toString().toUpperCase());
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            logger.info("exception {} ExtendDao get list Customer_reg", e);
            return null;
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
    public List<Map<String, String>> create_nv_temp(String act, String menuId, String threadId, String type) throws SQLException, BusinessException {
        return null;
    }
}

