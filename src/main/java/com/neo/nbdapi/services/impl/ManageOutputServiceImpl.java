package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.entity.UserInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import com.neo.nbdapi.services.ManageOutputService;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.services.objsearch.SearchOutputsManger;
import com.neo.nbdapi.services.objsearch.SearchUsesManager;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Date;
import java.util.*;

@Service
public class ManageOutputServiceImpl implements ManageOutputService {

    private Logger logger = LogManager.getLogger(ManageOutputServiceImpl.class);

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


    @Override
    public DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        System.out.println("getListOutpust---------------");
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<StationTimeSeries> stationTimeSeriesList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("");

            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchOutputsManger objectSearch = objectMapper.readValue(search, SearchOutputsManger.class);
                    System.out.println("objectSearch.getTableproductName()---------------" +objectSearch.getTableproductName());
                    if (Strings.isNotEmpty(objectSearch.getTableproductName())) {
                        sql.append("select * from (select st.*,ot.object_type_shortname,to_char(pr.timestamp,'DD/MM/YYYY HH24:MI:SS') timestampChar,pr.timestamp,pr.value,pr.warning,pr.manual,pr.create_user from station_time_series st");
                        sql.append(" join " + objectSearch.getTableproductName() + " pr on st.ts_id = pr.ts_id join stations_object_type sot on st.station_id = sot.station_id");
                        sql.append(" join object_type ot on sot.object_type_id = ot.object_type_id ) where 1=1");

                        if (Strings.isNotEmpty(objectSearch.getStation_type_name())) {
                            sql.append(" AND object_type_shortname like ? ");
                            paramSearch.add("%" + objectSearch.getStation_type_name() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getStations_no())) {
                            sql.append(" AND station_no LIKE ? ");
                            paramSearch.add("%" + objectSearch.getStations_no() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getStations_name())) {
                            sql.append(" AND station_name like ? ");
                            paramSearch.add("%" + objectSearch.getStations_name() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getParameter_type_name())) {
                            sql.append(" AND parametertype_name like ? ");
                            paramSearch.add("%" + objectSearch.getParameter_type_name() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getReponse())) {
                            sql.append(" AND value = ? ");
                            paramSearch.add("%" + objectSearch.getReponse() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getArea())) {
                            sql.append(" AND site_name like ? ");
                            paramSearch.add("%" + objectSearch.getArea() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getUser_create())) {
                            sql.append(" AND create_user like ? ");
                            paramSearch.add("%" + objectSearch.getUser_create() + "%");
                        }
//                        if (Strings.isNotEmpty(objectSearch.getCreatedBy())) {
//                            sql.append(" AND  create_user like ? ");
//                            paramSearch.add("%" + objectSearch.getCreatedBy() + "%");
//                        }
//                    if (Strings.isNotEmpty(objectSearch.getTimereponse())) {
//                        sql.append(" AND  timestamp like ? ");
//                        paramSearch.add("%" + objectSearch.getTimereponse() + "%");
//                    }
                        if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                            sql.append(" and timestamp >= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                            paramSearch.add(objectSearch.getFromDate());
                        }
                        if (Strings.isNotEmpty(objectSearch.getToDate())) {
                            sql.append(" and timestamp <= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                            paramSearch.add(objectSearch.getToDate());
                        }
                        sql.append(" order by timestamp desc");
                    }else{
                        sql.append("select * from (select st.*,ot.object_type_shortname,to_char(pr.timestamp,'DD/MM/YYYY HH:MI:SS') timestampChar,pr.timestamp,pr.value,pr.warning,pr.manual,pr.create_user from station_time_series st");
                        sql.append(" join temperature pr on st.ts_id = pr.ts_id join stations_object_type sot on st.station_id = sot.station_id");
                        sql.append(" join object_type ot on sot.object_type_id = ot.object_type_id ) where rownum < 1");
                    }
                    System.out.println("objectSearch.getToDate())---------------" +objectSearch.getToDate());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("sql---------------" +sql);
            }
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                StationTimeSeries stationTimeSeries = StationTimeSeries.builder()
                        .objectTypeShortName(resultSetListData.getString("object_type_shortname"))
                        .stationNo(resultSetListData.getString("station_no"))
                        .stationName(resultSetListData.getString("station_name"))
                        .parameterTypeName(resultSetListData.getString("parametertype_name"))
                        .PrValue(resultSetListData.getInt("value"))
                        .prTimestamp(resultSetListData.getString("timestampChar"))
                        .siteName(resultSetListData.getString("site_name"))
                        .PrWarning(resultSetListData.getInt("warning"))
                        .PrCreatedUser(resultSetListData.getString("create_user"))
                        .build();
                stationTimeSeriesList.add(stationTimeSeries);

            }
            logger.debug("stationTimeSeries1", stationTimeSeriesList);
            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(stationTimeSeriesList.size())
                    .recordsTotal(total)
                    .content(stationTimeSeriesList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(stationTimeSeriesList)
                    .build();
        }
    }

    @Override
    public List<ComboBox> getListStations(String userId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder(" select station_id,station_code,station_name from stations where status = 1 and rownum < 100 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder()
                    .id(-1L)
                    .text("Lựa chọn")
                    .build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .idStr(rs.getString("station_id"))
                        .text(rs.getString("station_code") + " - " + rs.getString("station_name"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }

    @Override
    public List<ComboBox> getListParameterByStations(String stationId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder(" select * from (select pt.parameter_type_id,pt.parameter_type_name,s.station_id,s.station_name from parameter p  join parameter_type  pt on p.parameter_type_id = pt.parameter_type_id join stations s on p.station_id = s.station_id) where  station_id = ? ");
        try (Connection connection = ds.getConnection();
             PreparedStatement st = connection.prepareStatement(sql.toString());) {
             List<Object> paramSearch = new ArrayList<>();
             logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
             st.setString(1, stationId);
             ResultSet rs = st.executeQuery();
             List<ComboBox> list = new ArrayList<>();
             ComboBox stationType = ComboBox.builder()
                    .id(-1L)
                    .text("Lựa chọn")
                    .build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("parameter_type_id"))
                        .text(rs.getString("parameter_type_name"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }

    @Override
    public String getSqlStatement(String stationId, String parameterTypeId, String fromDate, String toDate) throws SQLException, BusinessException {
        String proc = "begin ?:= MANAGER_OUTPUTS.get_outputs_lst(?,?,?,?) ;end;";
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
            ps.setString(2, stationId);
            ps.setString(3, parameterTypeId);
            ps.setString(4, fromDate);
            ps.setString(5, toDate);
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

