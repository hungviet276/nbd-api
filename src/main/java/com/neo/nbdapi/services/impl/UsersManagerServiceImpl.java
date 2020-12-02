package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.CasbinRule;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.entity.UserInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.services.objsearch.SearchMailConfig;
import com.neo.nbdapi.services.objsearch.SearchUsesManager;
import com.zaxxer.hikari.HikariDataSource;
import lombok.ToString;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Enforcer enforcer;

    @Override
    public DefaultPaginationDTO getListUsersPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
            logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
            List<UserInfo> userInfoList = new ArrayList<>();
            try (Connection connection = ds.getConnection()) {
                int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
                int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
                String search = defaultRequestPagingVM.getSearch();

//                StringBuilder sql = new StringBuilder("select * from( SELECT u.id,u.password,u.name,u.mobile,u.position,u.email,u.gender,case when u.gender = 1 then 'Nam' else 'Nữ' end genders ,case when u.status_id = 1 then 'Hoạt động' else 'Không hoạt động' end status,TO_CHAR(u.created_date,'dd/mm/yyyy') createdDate,u.check_role,u.card_number,u.code,u.office_code,u.date_role,u.created_by,u.status_id,u.created_date,g.name group_name,u.is_delete FROM user_info u join group_user_info g on u.office_code = g.id ) where is_delete = 0")select * from( SELECT u.id,u.password,u.name,u.mobile,u.position,u.email,u.gender,case when u.gender = 1 then 'Nam' else 'Nữ' end genders ,case when u.status_id = 1 then 'Hoạt động' else 'Không hoạt động' end status,TO_CHAR(u.created_date,'dd/mm/yyyy') createdDate,u.check_role,u.card_number,u.code,u.office_code,u.date_role,u.created_by,u.status_id,u.created_date,g.name group_name,u.is_delete FROM user_info u join group_user_info g on u.office_code = g.id ) where is_delete = 0");
                StringBuilder sql = new StringBuilder("select id,password,name,mobile,position,email,gender,genders,status,createdDate,check_role,card_number,code,office_code,date_role,created_by,status_id,created_date,is_delete,LISTAGG(group_name, ', ') WITHIN GROUP (ORDER BY group_name) group_names from( SELECT u.id,u.password,u.name,u.mobile,u.position,u.email,u.gender,case when u.gender = 1 then 'Nam' else 'Nữ' end genders ,case when u.status_id = 1 then 'Hoạt động' else 'Không hoạt động' end status,TO_CHAR(u.created_date,'dd/mm/yyyy') createdDate,u.check_role,u.card_number,u.code,u.office_code,u.date_role,u.created_by,u.status_id,u.created_date,g.name group_name,u.is_delete");
                sql.append(" FROM user_info u left join group_detail gd on u.id = gd.user_info_id left join group_user_info g on gd.group_id = g.id ) where is_delete = 0 ");
                List<Object> paramSearch = new ArrayList<>();
                logger.debug("Object search: {}", search);
                if (Strings.isNotEmpty(search)) {
                    try {
                        SearchUsesManager objectSearch = objectMapper.readValue(search, SearchUsesManager.class);
                        if (Strings.isNotEmpty(objectSearch.getId())) {
                            sql.append(" AND id like ? ");
                            paramSearch.add("%" +objectSearch.getId()+ "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getName())) {
                            sql.append(" AND name LIKE ? ");
                            paramSearch.add("%" + objectSearch.getName() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getMobile())) {
                            sql.append(" AND mobile like ? ");
                            paramSearch.add("%" + objectSearch.getMobile() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getEmail())) {
                            sql.append(" AND email like ? ");
                            paramSearch.add("%" + objectSearch.getEmail() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getGender())) {
                            sql.append(" AND gender = ? ");
                            paramSearch.add(objectSearch.getGender());
                        }
//                        if (Strings.isNotEmpty(objectSearch.getCheckRole())) {
//                            sql.append(" AND group_name like ? ");
//                            paramSearch.addCasbin("%" + objectSearch.getCheckRole() + "%");
//                        }
                        if (Strings.isNotEmpty(objectSearch.getCardNumber())) {
                            sql.append(" AND card_number like ? ");
                            paramSearch.add("%" + objectSearch.getCardNumber() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getOfficeCode())) {
                            sql.append(" AND group_name like ? ");
                            paramSearch.add("%" + objectSearch.getOfficeCode() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getCreatedBy())) {
                            sql.append(" AND  created_by like ? ");
                            paramSearch.add("%" + objectSearch.getCreatedBy() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getDatedwl())) {
                            sql.append(" AND  date_role like ? ");
                            paramSearch.add("%" + objectSearch.getDatedwl() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getStatusId())) {
                            sql.append(" AND  status_id = ? ");
                            paramSearch.add(objectSearch.getStatusId());
                        }
                        if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                            sql.append(" and created_date >= TO_DATE (?, 'dd/mm/yyyy')");
                            paramSearch.add(objectSearch.getFromDate());
                        }
                        if (Strings.isNotEmpty(objectSearch.getToDate())) {
                            sql.append(" and created_date <= TO_DATE (?, 'dd/mm/yyyy')");
                            paramSearch.add(objectSearch.getToDate());
                        }
                        sql.append(" GROUP BY id,password,name,mobile,position,email,gender,genders,status,createdDate,check_role,card_number,code,office_code,date_role,created_by,status_id,created_date,is_delete order by created_date desc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("sql---------------" +sql);
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
                            .genders(resultSetListData.getString("genders"))
                            .statusIds(resultSetListData.getString("status"))
                            .statusId(resultSetListData.getInt("status_id"))
                            .checkRole(resultSetListData.getInt("check_role"))
                            .code(resultSetListData.getString("code"))
                            .officeCode(resultSetListData.getString("group_names"))
                            .createdDates(resultSetListData.getString("createdDate"))
                            .createdDate(resultSetListData.getDate("created_date"))
                            .cardNumbers(resultSetListData.getString("card_number"))
                            .dateRole(resultSetListData.getString("date_role"))
                            .createdBy(resultSetListData.getString("created_by"))
                            .group_id(resultSetListData.getInt("office_code"))
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
                        map.put(colNames.get(i - 1), "");
                    } else
                        map.put(colNames.get(i - 1), rs.getObject(i).toString());
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
    public String create_nv_temp(String act, String menuId, String threadId, String type) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.new_menu_act_pro(?,?,?,?) ;end;";
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
            ps.setString(2, act);
            ps.setString(3, menuId);
            ps.setString(4, threadId);
            ps.setString(5, type);
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
    public String create_nq_temp(String nhomquyen_id, String threadId, String type, String checkall) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.news_role_temp(?,?,?,?) ;end;";
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
            ps.setString(2, nhomquyen_id);
            ps.setString(3, threadId);
            ps.setString(4, type);
            ps.setString(5, checkall);
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
    public List<ComboBox> get_list_group_users() throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select * from GROUP_USER_INFO ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType;
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("ID"))
                        .text(rs.getString("ID") + " - " + rs.getString("NAME"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }

    @Override
    public String createUser(UsersManagerVM usersManagerVM) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.new_user_info(?,?,?,?,?,?,?,?,?,?,?,?,?) ;end;";
        String casbinSql = "select DISTINCT mad.URL_API,mad.ptype,trim(?) username,mad.method from\n" +
                " user_menu_act_pro utp,menu_act_detail mad where utp.menu_id = mad.menu_id\n" +
                " and utp.act = mad.act and thread_id = ?";
        if(usersManagerVM.getCheck_roke() == 1){
            casbinSql = "select ptype,trim(?) username,method,url_api from (select DISTINCT a.menu_id,a.act,mad.ptype,mad.url_api,mad.METHOD from \n" +
                    "        (select  mac.menu_id,mac.act from permission_group_temp u,menu_access_act mac\n" +
                    "        where u.role_id = mac.role_id and u.thread_id = ? ) a,menu_act_detail mad where a.menu_id = mad.menu_id and a.act = mad.act)";
        }
        ResultSet rs = null;
        List<CasbinRule> casbinRules = new ArrayList<>();
        try(Connection conn = ds.getConnection();
            CallableStatement cs = conn.prepareCall(proc);
            PreparedStatement st = conn.prepareStatement(casbinSql);) {
            Long valueId = new Date().getTime();
            int i = 1;
            cs.registerOutParameter(i++, OracleTypes.VARCHAR);
            cs.setString(i++, usersManagerVM.getId());
            cs.setString(i++, passwordEncoder.encode(usersManagerVM.getPassword()));
            cs.setString(i++, usersManagerVM.getName());
            cs.setString(i++, usersManagerVM.getMobile());
            cs.setString(i++, usersManagerVM.getEmail());
            cs.setInt(i++, usersManagerVM.getGender());
            cs.setInt(i++, usersManagerVM.getStatus_id());
            cs.setInt(i++, usersManagerVM.getCheck_roke());
            cs.setString(i++, usersManagerVM.getCard_number());
            cs.setInt(i++, usersManagerVM.getGroup_user_id());
            cs.setString(i++, usersManagerVM.getCheck_download_time());
            cs.setString(i++, usersManagerVM.getThread_id());
            cs.setString(i++, usersManagerVM.getUser_login());
            cs.execute();
            String result = cs.getString(1);

            if("true".equals(result)){
                //cap nhat vao casbin rule
                st.setString(1,usersManagerVM.getId());
                st.setString(2,usersManagerVM.getThread_id());
                rs = st.executeQuery();
                while (rs.next()){
                    CasbinRule bo = CasbinRule.builder()
                            .pType(rs.getString("PTYPE"))
                            .v0(rs.getString("username"))
                            .v1(rs.getString("URL_API"))
                            .v2(rs.getString("method"))
                            .v3(null)
                            .v4(null)
                            .v5(null)
                            .build();
                    casbinRules.add(bo);
                }
                boolean isSuccess = false;
                for (CasbinRule casbinRule : casbinRules) {
                    isSuccess = enforcer.addPolicy(casbinRule.getV0(), casbinRule.getV1(), casbinRule.getV2());
                    if (isSuccess) enforcer.savePolicy();
                }
                rs.close();
                logger.info("add casbin : " + isSuccess);
            }

            return result;
        } catch (Exception e) {
            logger.info("exception {} ExtendDao get list Customer_reg", e);
            return "false";
        }
    }

    @Override
    public String editUsers(UsersManagerVM usersManagerVM) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.edit_user_info(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ;end;";
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
            ps.setString(2, usersManagerVM.getId());
            ps.setString(3, passwordEncoder.encode(usersManagerVM.getPassword()));
            ps.setString(4, usersManagerVM.getName());
            ps.setString(5, usersManagerVM.getMobile());
            ps.setString(6, usersManagerVM.getEmail());
            ps.setInt(7, usersManagerVM.getGender());
            ps.setInt(8, usersManagerVM.getStatus_id());
            ps.setInt(9, usersManagerVM.getCheck_roke());
            ps.setString(10, usersManagerVM.getCard_number());
            ps.setInt(11, usersManagerVM.getGroup_user_id());
            ps.setString(12, usersManagerVM.getCheck_download_time());
            ps.setString(13, usersManagerVM.getThread_id());
            ps.setString(14, usersManagerVM.getUser_login());
            ps.setString(15, usersManagerVM.getCheck_edit_pass());
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
    public String deleteTemp(String tempId, String threadId) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.delete_temp(?,?) ;end;";
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
            ps.setString(2, tempId);
            ps.setString(3, threadId);
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
    public String deleteUsers(String username) throws SQLException, BusinessException {
        String proc = "begin ?:= users_manage.delete_user_info(?) ;end;";
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
            ps.setString(2, username);
            ps.execute();
            String result = ps.getString(1);
            System.out.println("result deleteUsers ---- " +result );
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
    public DefaultResponseDTO   deleteUsersMutil( List<String> ids) throws SQLException {
            String sqlDeleteManager = "update user_info set is_delete = 1 where id =?";

            Connection connection = ds.getConnection();
            try{
                connection.setAutoCommit(false);
                PreparedStatement stmDetateManager = connection.prepareStatement(sqlDeleteManager);

                for (String tmp: ids) {
                    System.out.println("ids======================"+ ids);
                    stmDetateManager.setString(1, tmp);
                    stmDetateManager.addBatch();
                }
                stmDetateManager.executeBatch();

                connection.commit();

            } catch (Exception e){
                connection.rollback();
                logger.error("deleteUsersMutil exception : {}", e.getMessage());
                return DefaultResponseDTO.builder().status(0).message("Không thành công").build();

            } finally {
                connection.close();
            }

            return DefaultResponseDTO.builder().status(1).message("Thành công").build();

    }

}

