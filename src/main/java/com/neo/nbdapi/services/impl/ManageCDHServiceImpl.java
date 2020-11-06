package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.LogCDH;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.ManageCDHService;
import com.neo.nbdapi.services.ManageOutputService;
import com.neo.nbdapi.services.objsearch.SearchCDHHistory;
import com.neo.nbdapi.services.objsearch.SearchOutputsManger;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ManageCDHServiceImpl implements ManageCDHService {

    private Logger logger = LogManager.getLogger(ManageCDHServiceImpl.class);

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
        List<LogCDH> logCDHList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("");

            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchCDHHistory objectSearch = objectMapper.readValue(search, SearchCDHHistory.class);
                        if (Strings.isNotEmpty(objectSearch.getStation_id()) && Strings.isNotEmpty(objectSearch.getValueType_id())) {
                            sql.append("select * from (select st.station_code,st.station_name,pt.parameter_type_name,sf.station_id,sf.value_type_id,sf.created_by_id,lc.status,lc.end_pust_time,to_char(lc.end_pust_time,'DD/MM/YYYY HH:MI:SS') timestampChar,case when lc.status = 1 then 'Hoạt động' else 'Không hoạt động' end statusStr,lc.description from log_cdh lc join station_files sf on lc.stations_file_id = sf.station_file_id  join stations st on sf.station_id = st.station_id join parameter_type pt on sf.value_type_id = pt.parameter_type_id) where 1=1 ");
                            if (Strings.isNotEmpty(objectSearch.getStation_id())) {
                                sql.append(" and station_id=?");
                                paramSearch.add(objectSearch.getStation_id());
                            }
                            if (Strings.isNotEmpty(objectSearch.getValueType_id())) {
                                sql.append(" and value_type_id=?");
                                paramSearch.add(objectSearch.getValueType_id());
                            }
                            if (Strings.isNotEmpty(objectSearch.getStation_no())) {
                                sql.append(" AND station_code like ? ");
                                paramSearch.add("%" + objectSearch.getStation_no() + "%");
                            }
                            if (Strings.isNotEmpty(objectSearch.getStation_name())) {
                                sql.append(" AND station_name LIKE ? ");
                                paramSearch.add("%" + objectSearch.getStation_name() + "%");
                            }
                            if (Strings.isNotEmpty(objectSearch.getParameterName())) {
                                sql.append(" AND parameter_type_name like ? ");
                                paramSearch.add("%" + objectSearch.getParameterName() + "%");
                            }
                            if (Strings.isNotEmpty(objectSearch.getCreateModify())) {
                                sql.append(" AND created_by_id like ? ");
                                paramSearch.add("%" + objectSearch.getCreateModify() + "%");
                            }
                            System.out.println("objectSearch.getStatus()---------------" +objectSearch.getStatus());
                            if (Strings.isNotEmpty(objectSearch.getStatus())) {
                                sql.append(" AND status = ? ");
                                paramSearch.add(objectSearch.getStatus());
                            }
                            if (Strings.isNotEmpty(objectSearch.getNote())) {
                                sql.append(" AND description like ? ");
                                paramSearch.add("%" + objectSearch.getNote() + "%");
                            }

                            if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                                sql.append(" and end_pust_time >= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                                paramSearch.add(objectSearch.getFromDate());
                            }
                            if (Strings.isNotEmpty(objectSearch.getToDate())) {
                                sql.append(" and end_pust_time <= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                                paramSearch.add(objectSearch.getToDate());
                            }
                            if (Strings.isNotEmpty(objectSearch.getUserCreate())) {
                                sql.append(" and created_by_id like ?");
                                paramSearch.add("%" + objectSearch.getUserCreate() + "%");
                            }
                            sql.append(" order by end_pust_time desc");
                        }else{
                            sql.append("select * from (select st.station_code,st.station_name,pt.parameter_type_name,sf.station_id,sf.value_type_id,sf.created_by_id,lc.end_pust_time,to_char(lc.end_pust_time,'DD/MM/YYYY HH:MI:SS') timestampChar,case when lc.status = 1 then 'Hoạt động' else 'Không hoạt động' end statusStr,lc.description from log_cdh lc join station_files sf on lc.stations_file_id = sf.station_file_id  join stations st on sf.station_id = st.station_id join parameter_type pt on sf.value_type_id = pt.parameter_type_id) where rownum < 1");
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("sql---------------" +sql);
            }
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                LogCDH logCDH = LogCDH.builder()
                        .stationCode(resultSetListData.getString("station_code"))
                        .stationName(resultSetListData.getString("station_name"))
                        .valueTypeName(resultSetListData.getString("parameter_type_name"))
                        .createdUser(resultSetListData.getString("created_by_id"))
                        .statusStr(resultSetListData.getString("statusStr"))
                        .description(resultSetListData.getString("description"))
                        .endPustTimeStr(resultSetListData.getString("timestampChar"))
                        .build();
                logCDHList.add(logCDH);

            }
            logger.debug("logCDHList", logCDHList);
            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(logCDHList.size())
                    .recordsTotal(total)
                    .content(logCDHList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(logCDHList)
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
                        .id(rs.getLong("station_id"))
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

}

