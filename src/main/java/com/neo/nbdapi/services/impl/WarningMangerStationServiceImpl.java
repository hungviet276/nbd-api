package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.WarningManagerStationDAO;
import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningManagerStation;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.*;
import com.neo.nbdapi.services.WarningMangerStationService;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.services.objsearch.SearchNotificationHistory;
import com.neo.nbdapi.services.objsearch.WarningManagerStationSearch;
import com.neo.nbdapi.utils.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class WarningMangerStationServiceImpl implements WarningMangerStationService {
    private Logger logger = LogManager.getLogger(ConfigValueTypeServiceImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private WarningManagerStationDAO warningManagerStationDAO;

    @Override
    public DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<WarningManagerStation> configValueTypes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            logger.debug("mailConfigVM: {}", defaultRequestPagingVM);
            // start = pageNumber, lenght = recordPerPage
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select w.id, s.station_id, s.station_name, w.code, w.name, w.icon, w.created_at, w.description, w.content, w.color, w.suffixes_table from warning_manage_stations w inner join stations s on s.STATION_ID = w.STATION_ID where s.isdel = 0 and s.IS_ACTIVE = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {

                WarningManagerStationSearch objectSearch = objectMapper.readValue(search, WarningManagerStationSearch.class);
                if (objectSearch.getId() != null) {
                    sql.append(" AND w.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationId())) {
                    sql.append(" AND s.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationName())) {
                    sql.append(" AND UPPER(s.station_name) like ? ");
                    paramSearch.add("%" + objectSearch.getStationName().toUpperCase() + "%");
                }
                if (Strings.isNotEmpty(objectSearch.getWarningCode())) {
                    sql.append(" AND UPPER(w.code) LIKE ? ");
                    paramSearch.add("%" + objectSearch.getWarningCode().toUpperCase() + "%");
                }
                if (Strings.isNotEmpty(objectSearch.getWarningName())) {
                    sql.append(" AND UPPER(w.name) like ? ");
                    paramSearch.add("%" + objectSearch.getWarningName().toUpperCase() + "%");
                }
                if (Strings.isNotEmpty(objectSearch.getIcon())) {
                    sql.append(" AND w.icon = ? ");
                    paramSearch.add(objectSearch.getIcon());
                }
                if (Strings.isNotEmpty(objectSearch.getStartDate())) {
                    sql.append(" and trunc(w.created_at) >= trunc(TO_DATE(?, 'dd/mm/yyyy')) ");
                    paramSearch.add(objectSearch.getStartDate());
                }
                if (Strings.isNotEmpty(objectSearch.getEndDate())) {
                    sql.append(" and trunc(w.created_at) <= trunc(TO_DATE(?, 'dd/mm/yyyy')) ");
                    paramSearch.add(objectSearch.getEndDate());
                }
                if (Strings.isNotEmpty(objectSearch.getSuffixesTable())) {
                    sql.append(" AND w.suffixes_table = ? ");
                    paramSearch.add(objectSearch.getSuffixesTable());
                }
            }
            sql.append(" ORDER BY w.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                WarningManagerStation warningManagerStation = WarningManagerStation.builder().
                        id(resultSetListData.getLong("id"))
                        .stationId(resultSetListData.getString("station_id"))
                        .stationName(resultSetListData.getString("station_name"))
                        .warningCode(resultSetListData.getString("code"))
                        .warningName(resultSetListData.getString("name"))
                        .icon(resultSetListData.getString("icon"))
                        .createDate(resultSetListData.getDate("created_at"))
                        .description(resultSetListData.getString("description"))
                        .content(resultSetListData.getString("content"))
                        .typeWarning(resultSetListData.getString("suffixes_table"))
                        .color(resultSetListData.getString("color"))
                        .build();

                configValueTypes.add(warningManagerStation);
            }

            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(configValueTypes.size())
                    .recordsTotal(total)
                    .content(configValueTypes)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(configValueTypes)
                    .build();
        }
    }

    @Override
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerStrVM selectVM) throws SQLException {
        return warningManagerStationDAO.getListParameterWarningConfig(selectVM);
    }

    @Override
    public List<ComboBox> getListParameterWarningThreshold(SelectWarningManagerVM selectVM) throws SQLException {
        return warningManagerStationDAO.getListParameterWarningThreshold(selectVM);
    }

    @Override
    public WarningThresholdINF getInFoWarningThreshold(Long idThreshold) throws SQLException {
        return warningManagerStationDAO.getInFoWarningThreshold(idThreshold);
    }

    @Override
    public DefaultResponseDTO createWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException {
        return warningManagerStationDAO.createWarningManagerStation(warningManagerStationDTO);
    }

    @Override
    public List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(Long WarningManageStationId) throws SQLException {
        return warningManagerStationDAO.getWarningMangerDetailInfoDTOs(WarningManageStationId);
    }

    @Override
    public DefaultResponseDTO editWarningManagerStation(WarningManagerStationDTO warningManagerStationDTO) throws SQLException {

        List<WarningMangerDetailInfoDTO> warningManagerDetailDTOCurrents = warningManagerStationDAO.getWarningMangerDetailInfoDTOs(warningManagerStationDTO.getId());

        List<WarningManagerDetailDTO> deletes = new ArrayList<>();

        List<WarningManagerDetailDTO> creates = new ArrayList<>();

        List<WarningManagerDetailDTO> inputs = warningManagerStationDTO.getDataWarning();

        for (WarningManagerDetailDTO out : inputs) {
            int insert = 1;
            for (WarningMangerDetailInfoDTO in : warningManagerDetailDTOCurrents) {
                if (out.getWarningThresholdId() == in.getIdWarningThreshold()) {
                    insert = 0;
                    break;
                }
            }
            if (insert == 1) {
                creates.add(out);
            }
        }
        for (WarningMangerDetailInfoDTO out : warningManagerDetailDTOCurrents) {
            int insert = 1;
            for (WarningManagerDetailDTO in : inputs) {
                if (out.getIdWarningThreshold() == in.getWarningThresholdId()) {
                    insert = 0;
                    break;
                }
            }
            deletes.add(WarningManagerDetailDTO.builder().id(out.getId()).build());
        }


        return warningManagerStationDAO.editWarningManagerStation(warningManagerStationDTO, deletes, creates);
    }

    @Override
    public DefaultResponseDTO deleteWarningManagerStation(List<Long> id) throws SQLException {
        return warningManagerStationDAO.deleteWarningManagerStation(id);
    }

    @Override
    public List<ComboBoxStr> getWarningComboBox(SelectWarningManagerStrVM selectVM) throws SQLException {
        return warningManagerStationDAO.getWarningComboBox(selectVM);
    }

    // get list notification today
    @Override
    public List<NotificationToDayDTO> getListNotificationToday() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        String endDate = DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy HH:mm");
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        String startDate = DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy HH:mm");

        return warningManagerStationDAO.getListWarningManagerStationByDate(startDate, endDate);
    }

    @Override
    public NotificationToDayDTO getNotificationById(Long notificationHistoryId) throws SQLException {
        return warningManagerStationDAO.getWarningManagerStationById(notificationHistoryId);
    }

    // function get list warning station history
    @Override
    public DefaultPaginationDTO getWarningStationHistory(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<WarningStationHistoryDTO> warningStationHistoryDTOList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            // pageNumber = start, recordPerPage = length
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("SELECT nh.id AS notification_history_id , wms.name AS warning_name, nh.push_timestap FROM notification_history nh JOIN warning_recipents wr ON nh.warning_recipents_id = wr.id JOIN warning_manage_stations wms ON wms.id = wr.manage_warning_stations JOIN stations st ON st.station_id = wms.station_id WHERE st.station_id = ? AND nh.push_timestap >= to_date(?, 'dd/mm/yyyy') AND nh.push_timestap <= to_date(?, 'dd/mm/yyyy')");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set param query to sql
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchNotificationHistory objectSearch = objectMapper.readValue(search, SearchNotificationHistory.class);
                    if (objectSearch == null)
                        throw new BusinessException("Dữ liệu tìm kiếm không hợp lệ");

                    logger.debug("Search object WarningStationHistory: {}", objectSearch);

                    if (Strings.isEmpty(objectSearch.getStationId()))
                        throw new BusinessException("Mã trạm không hợp lệ");

                    paramSearch.add(objectSearch.getStationId());

                    if (Strings.isEmpty(objectSearch.getFromDate()))
                        throw new BusinessException("Ngày bắt đầu không hợp lệ");

                    if (Strings.isNotEmpty(objectSearch.getFromDate())) {
                        if (!DateUtils.isValid(objectSearch.getFromDate(), DateUtils.DEFAULT_DATE_FORMAT))
                            throw new BusinessException("Ngày bắt đầu không hợp lệ, định dạng dd/mm/yyyy");
                    }

                    paramSearch.add(objectSearch.getFromDate());

                    if (Strings.isNotEmpty(objectSearch.getToDate())) {
                        if (!DateUtils.isValid(objectSearch.getToDate(), DateUtils.DEFAULT_DATE_FORMAT))
                            throw new BusinessException("Ngày kết thúc không hợp lệ, định dạng dd/mm/yyyy");
                        if (DateUtils.getDateFromStringFormat(objectSearch.getToDate(), "dd/MM/yyyy")
                                .before(DateUtils.getDateFromStringFormat(objectSearch.getFromDate(), "dd/MM/yyyy")))  {
                            throw new BusinessException("Ngày kết thúc phải lớn hơn ngày bắt đầu");
                        }
                        paramSearch.add(objectSearch.getToDate());

                    } else {
                        paramSearch.add(DateUtils.getStringFromDateFormat(new Date(), "dd/MM/yyyy"));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sql.append(" ORDER BY nh.push_timestap DESC");
            logger.debug("SQL QUERY: {}", sql);
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            // get result query by paging
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                WarningStationHistoryDTO warningStationHistoryDTO = WarningStationHistoryDTO
                        .builder()
                        .notificationHistoryId(resultSetListData.getLong("notification_history_id"))
                        .warningName(resultSetListData.getString("warning_name"))
                        .pushTimestamp(DateUtils.getStringFromDateFormat(resultSetListData.getDate("push_timestap"), "dd/MM/yyyy HH:ss"))
                        .build();
                warningStationHistoryDTOList.add(warningStationHistoryDTO);
            }

            // count result, totalElements
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(warningStationHistoryDTOList.size())
                    .recordsTotal(total)
                    .content(warningStationHistoryDTOList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(warningStationHistoryDTOList)
                    .build();
        }
    }
}
