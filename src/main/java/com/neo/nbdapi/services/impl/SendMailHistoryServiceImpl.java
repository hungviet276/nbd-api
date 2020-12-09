package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.NotificationHistory;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.SendMailHistoryService;
import com.neo.nbdapi.services.objsearch.SearchSendMailHistory;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendMailHistoryServiceImpl implements SendMailHistoryService {

    private Logger logger = LogManager.getLogger(SendMailHistoryServiceImpl.class);

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

    private NotificationHistory noficationHistory;

    @Override
    public DefaultPaginationDTO getListOutpust(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        System.out.println("getListOutpust---------------");
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<NotificationHistory> noficationHistoryList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("");

            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchSendMailHistory objectSearch = objectMapper.readValue(search, SearchSendMailHistory.class);
                    if (Strings.isNotEmpty(objectSearch.getStationId()) && Strings.isNotEmpty(objectSearch.getWarningId())) {
                        sql.append("select * from (select wms.id,wms.code,wms.name warning_name,wms.description,nh.push_timestap,to_char(nh.push_timestap,'DD/MM/YYYY HH:MI:SS') timestampChar,nh.status,s.station_code,s.station_name,s.station_id,grm.name gr_mail_name from notification_history nh join warning_recipents wr on nh.warning_recipents_id = wr.id  join group_receive_mail grm on   grm.id = wr.group_receive_mail_id join warning_manage_stations wms on wms.id = wr.manage_warning_stations join stations s on s.station_id = wms.station_id ) where 1=1");
                        if (Strings.isNotEmpty(objectSearch.getStation_no())) {
                            sql.append(" and station_code  like ?");
                            paramSearch.add("%" +objectSearch.getStation_no()+ "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getStation_name())) {
                            sql.append(" and station_name like ?");
                            paramSearch.add("%" +objectSearch.getStation_name() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getNote())) {
                            sql.append(" AND description like ? ");
                            paramSearch.add("%" + objectSearch.getNote() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getWarningCode())) {
                            sql.append(" AND code LIKE ? ");
                            paramSearch.add("%" + objectSearch.getWarningCode() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getWarningName())) {
                            sql.append(" AND warning_name like ? ");
                            paramSearch.add("%" + objectSearch.getWarningName() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getStationId())) {
                            sql.append(" AND station_id = ? ");
                            paramSearch.add(objectSearch.getStationId());
                        }
                        if (Strings.isNotEmpty(objectSearch.getWarningId())) {
                            sql.append(" AND id = ? ");
                            paramSearch.add(objectSearch.getWarningId());
                        }
                        if (Strings.isNotEmpty(objectSearch.getGroupMail())) {
                            sql.append(" and gr_mail_name like ?");
                            paramSearch.add("%" + objectSearch.getGroupMail() + "%");
                        }
                        if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                            sql.append(" and push_timestap >= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                            paramSearch.add(objectSearch.getFromDate());
                        }
                        if (Strings.isNotEmpty(objectSearch.getToDate())) {
                            sql.append(" and push_timestap <= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                            paramSearch.add(objectSearch.getToDate());
                        }
                        sql.append(" order by push_timestap desc");
                    }else{
                        sql.append("select * from (select wms.id,wms.code,wms.name,wms.description,nh.push_timestap,nh.status,s.station_code,s.station_name,s.station_id from notification_history nh join warning_recipents wr on nh.warning_recipents_id = wr.id join warning_manage_stations wms on wms.id = wr.manage_warning_stations join stations s on s.station_id = wms.station_id ) where rownum <1");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("sql---------------" +sql);
            }
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                noficationHistory = NotificationHistory.builder()
                        .stationNo(resultSetListData.getString("station_code"))
                        .stationName(resultSetListData.getString("station_name"))
                        .warningNo(resultSetListData.getString("code"))
                        .warningName(resultSetListData.getString("warning_name"))
                        .description(resultSetListData.getString("description"))
                        .pushTimestampStr(resultSetListData.getString("timestampChar"))
                        .groupReMailName(resultSetListData.getString("gr_mail_name"))
                        .build();
                noficationHistoryList.add(noficationHistory);

            }
            logger.debug("noficationHistoryList", noficationHistoryList);
            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(noficationHistoryList.size())
                    .recordsTotal(total)
                    .content(noficationHistoryList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(noficationHistoryList)
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
    public List<ComboBox> getLstWarningManagerByStationId(String stationId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder(" select id,code,name from warning_manage_stations where station_id = ?");
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
                        .id(rs.getLong("id"))
                        .text(rs.getString("code") + " - " + rs.getString("name"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }
    @Override
    public List<NotificationHistory> getListOutpust2(SearchSendMailHistory objectSearch) throws SQLException {
        StringBuilder sql = new StringBuilder("");
        List<Object> paramSearch = new ArrayList<>();
        // set param query to sql
        if (Strings.isNotEmpty(objectSearch.getStationId()) && Strings.isNotEmpty(objectSearch.getWarningId())) {
            sql.append("select * from (select wms.id,wms.code,wms.name warning_name,wms.description,nh.push_timestap,to_char(nh.push_timestap,'DD/MM/YYYY HH:MI:SS') timestampChar,nh.status,s.station_code,s.station_name,s.station_id,grm.name gr_mail_name from notification_history nh join warning_recipents wr on nh.warning_recipents_id = wr.id  join group_receive_mail grm on   grm.id = wr.group_receive_mail_id join warning_manage_stations wms on wms.id = wr.manage_warning_stations join stations s on s.station_id = wms.station_id ) where 1=1");
            if (Strings.isNotEmpty(objectSearch.getStation_no())) {
                sql.append(" and station_code  like ?");
                paramSearch.add("%" +objectSearch.getStation_no()+ "%");
            }
            if (Strings.isNotEmpty(objectSearch.getStation_name())) {
                sql.append(" and station_name like ?");
                paramSearch.add("%" +objectSearch.getStation_name() + "%");
            }
            if (Strings.isNotEmpty(objectSearch.getNote())) {
                sql.append(" AND description like ? ");
                paramSearch.add("%" + objectSearch.getNote() + "%");
            }
            if (Strings.isNotEmpty(objectSearch.getWarningCode())) {
                sql.append(" AND code LIKE ? ");
                paramSearch.add("%" + objectSearch.getWarningCode() + "%");
            }
            if (Strings.isNotEmpty(objectSearch.getWarningName())) {
                sql.append(" AND warning_name like ? ");
                paramSearch.add("%" + objectSearch.getWarningName() + "%");
            }
            if (Strings.isNotEmpty(objectSearch.getGroupMail())) {
                sql.append(" and gr_mail_name like ?");
                paramSearch.add("%" + objectSearch.getGroupMail() + "%");
            }
            if (Strings.isNotEmpty(objectSearch.getStationId())) {
                sql.append(" AND station_id = ? ");
                paramSearch.add(objectSearch.getStationId());
            }
            if (Strings.isNotEmpty(objectSearch.getWarningId())) {
                sql.append(" AND id = ? ");
                paramSearch.add(objectSearch.getWarningId());
            }

            if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                sql.append(" and push_timestap >= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                paramSearch.add(objectSearch.getFromDate());
            }
            if (Strings.isNotEmpty(objectSearch.getToDate())) {
                sql.append(" and push_timestap <= to_date(?, 'DD/MM/YYYY HH24:MI:SS')");
                paramSearch.add(objectSearch.getToDate());
            }
            sql.append(" order by push_timestap desc");
        }else{
            sql.append("select * from (select wms.id,wms.code,wms.name,wms.description,nh.push_timestap,nh.status,s.station_code,s.station_name,s.station_id from notification_history nh join warning_recipents wr on nh.warning_recipents_id = wr.id join warning_manage_stations wms on wms.id = wr.manage_warning_stations join stations s on s.station_id = wms.station_id ) where rownum <1");
        }
        System.out.println("sql sendmail history Export =" +sql);
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            for(int i = 0; i < paramSearch.size(); i++) {
                statement.setObject(i + 1, paramSearch.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            List<NotificationHistory> noficationHistoryList = new ArrayList<>();

            while (resultSet.next()) {
                noficationHistory = NotificationHistory.builder()
                        .stationNo(resultSet.getString("station_code"))
                        .stationName(resultSet.getString("station_name"))
                        .warningNo(resultSet.getString("code"))
                        .warningName(resultSet.getString("warning_name"))
                        .groupReMailName(resultSet.getString("gr_mail_name"))
                        .description(resultSet.getString("description"))
                        .pushTimestampStr(resultSet.getString("timestampChar"))
                        .build();
                noficationHistoryList.add(noficationHistory);
            }
            return noficationHistoryList;
        }
    }
    public SXSSFWorkbook export(SearchSendMailHistory objectSearch) throws SQLException {
        List<NotificationHistory> noficationHistoryList = getListOutpust2(objectSearch);
        System.out.println("noficationHistoryList =========" +noficationHistoryList);
        System.out.println("export SEND_MAIL_HISTORY running");
        // create streaming workbook optimize memory of apache poi
        int cellNum = 11;
        final SXSSFWorkbook workbook = new SXSSFWorkbook();
        final SXSSFSheet sheet = workbook.createSheet("SEND_MAIL_HISTORY");
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
        titleHeadRow.getCell((short)0).setCellValue("Lịch sử gửi mail cảnh báo");
        titleHeadRow.getCell((short)0).setCellStyle(style);
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));

        SXSSFRow header = sheet.createRow(2);
        for (int i = 0; i <=  cellNum; i++) {
            header.createCell((short)i);
            header.getCell((short)i).setCellStyle(style_column);
        }
        header.getCell((short)0).setCellValue("Mã trạm");
        header.getCell((short)1).setCellValue("Tên trạm");
        header.getCell((short)2).setCellValue("Mã loại cảnh báo");
        header.getCell((short)3).setCellValue("Tên loại cảnh báo");
        header.getCell((short)4).setCellValue("Nhóm nhận cảnh báo");
        header.getCell((short)5).setCellValue("Tiêu đề mail");
        header.getCell((short)6).setCellValue("Thời gian gửi mail");
        //end create header
//create content
        noficationHistoryList.forEach(logMail -> {
            SXSSFRow row = sheet.createRow(3);
            SXSSFCell cell0 = row.createCell(0, CellType.STRING);
            cell0.setCellValue(logMail.getStationNo());

            SXSSFCell cell1 = row.createCell(1, CellType.STRING);
            cell1.setCellValue(logMail.getStationName());

            SXSSFCell cell2 = row.createCell(2, CellType.STRING);
            cell2.setCellValue(logMail.getWarningNo());

            SXSSFCell cell3 = row.createCell(3, CellType.STRING);
            cell3.setCellValue(logMail.getWarningName());

            SXSSFCell cell4 = row.createCell(4, CellType.STRING);
            cell4.setCellValue(logMail.getGroupReMailName());

            SXSSFCell cell5 = row.createCell(5, CellType.STRING);
            cell5.setCellValue(logMail.getDescription());

            SXSSFCell cell6 = row.createCell(6, CellType.STRING);
            cell6.setCellValue(logMail.getPushTimestampStr());

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

