package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.LogActDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.LogActDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.LogCDH;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.ManageCDHService;
import com.neo.nbdapi.services.ManageOutputService;
import com.neo.nbdapi.services.objsearch.SearchCDHHistory;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.services.objsearch.SearchOutputsManger;
import com.neo.nbdapi.utils.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

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

    private LogCDH logCDH;

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
    public List<ComboBoxStr> getListStations(String userId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder(" select station_id,station_code,station_name from stations where status = 1 and rownum < 100 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBoxStr> list = new ArrayList<>();
            ComboBoxStr stationType = ComboBoxStr.builder()
                    .id("-1")
                    .text("Lựa chọn")
                    .build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBoxStr.builder()
                        .id(rs.getString("station_id"))
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
    public List<LogCDH> getListOutpust2(SearchCDHHistory objectSearch) throws SQLException {
        StringBuilder sql = new StringBuilder("");
        List<Object> paramSearch = new ArrayList<>();
        System.out.println("objectSearch.getStation_id() = " + objectSearch.getStation_id());
        System.out.println("objectSearch.getValueType_id() = " + objectSearch.getValueType_id());
        // set param query to sql
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
        System.out.println("sql CDH Export =" +sql);
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            for(int i = 0; i < paramSearch.size(); i++) {
                statement.setObject(i + 1, paramSearch.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            List<LogCDH> logCDHList = new ArrayList<>();

            while (resultSet.next()) {
                LogCDH logCDH = LogCDH.builder()
                        .stationCode(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .valueTypeName(resultSet.getString("parameter_type_name"))
                        .createdUser(resultSet.getString("created_by_id"))
                        .statusStr(resultSet.getString("statusStr"))
                        .description(resultSet.getString("description"))
                        .endPustTimeStr(resultSet.getString("timestampChar"))
                        .build();
                logCDHList.add(logCDH);
            }
            return logCDHList;
        }
    }
    public SXSSFWorkbook export(SearchCDHHistory objectSearch) throws SQLException {
        List<LogCDH> logCDHList = getListOutpust2(objectSearch);
        System.out.println("LogCDH =========" +logCDHList);
        System.out.println("export CDH running");
        // create streaming workbook optimize memory of apache poi
        int cellNum = 11;
        final SXSSFWorkbook workbook = new SXSSFWorkbook();
        final SXSSFSheet sheet = workbook.createSheet("CDH_LOG");
        sheet.trackAllColumnsForAutoSizing();

        //---------
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short)15);
        font.setBold (true);
        style.setFont(font);
        //-----------------
        XSSFCellStyle style_column = (XSSFCellStyle) workbook.createCellStyle();
        style_column.setAlignment(HorizontalAlignment.CENTER);
        Font font_column = workbook.createFont();
        font_column.setFontName("Arial");
        font_column.setFontHeightInPoints((short)10);
        font_column.setBold (true);
        style_column.setFont(font_column);
        //create title
        SXSSFRow titleHeadRow = sheet.createRow(0);
        titleHeadRow.createCell(0);
        titleHeadRow.getCell((short)0).setCellValue("Lịch sử cập nhật CDH");
        titleHeadRow.getCell((short)0).setCellStyle(style);
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));

        SXSSFRow header = sheet.createRow(2);
        for (int i = 0; i <=  cellNum; i++) {
            header.createCell((short)i);
            header.getCell((short)i).setCellStyle(style_column);
        }
        header.getCell((short)0).setCellValue("Mã trạm");
        header.getCell((short)1).setCellValue("Tên trạm");
        header.getCell((short)2).setCellValue("Yếu tố");
        header.getCell((short)3).setCellValue("Người cập nhật");
        header.getCell((short)4).setCellValue("Trạng thái cập nhật");
        header.getCell((short)5).setCellValue("Ghi chú");
        header.getCell((short)6).setCellValue("Thời gian cập nhật");
        //end create header
//create content
        logCDHList.forEach(logActDTO -> {
            SXSSFRow row = sheet.createRow(3);
            // ghi id cua log act
            SXSSFCell cell0 = row.createCell(0, CellType.STRING);
            cell0.setCellValue(logActDTO.getStationCode());

            // ghi ten menu cua log act
            SXSSFCell cell1 = row.createCell(1, CellType.STRING);
            cell1.setCellValue(logActDTO.getStationName());

            // ghi act cua log act
            SXSSFCell cell2 = row.createCell(2, CellType.STRING);
            cell2.setCellValue(logActDTO.getValueTypeName());

            // ghi account cua log act
            SXSSFCell cell3 = row.createCell(3, CellType.STRING);
            cell3.setCellValue(logActDTO.getCreatedUser());

            // ghi ngay tac dong cua log act
            SXSSFCell cell4 = row.createCell(4, CellType.STRING);
            cell4.setCellValue(logActDTO.getStatusStr());

            SXSSFCell cell5 = row.createCell(5, CellType.STRING);
            cell5.setCellValue(logActDTO.getDescription());

            SXSSFCell cell6 = row.createCell(6, CellType.STRING);
            cell6.setCellValue(logActDTO.getEndPustTimeStr());

        });
        sheet.autoSizeColumn((short)0);
        sheet.autoSizeColumn((short)1);
        sheet.autoSizeColumn((short)2);
        sheet.autoSizeColumn((short)3);
        sheet.autoSizeColumn((short)4);
        sheet.autoSizeColumn((short)5);
        sheet.autoSizeColumn((short)6);

        return workbook;
    }


}

