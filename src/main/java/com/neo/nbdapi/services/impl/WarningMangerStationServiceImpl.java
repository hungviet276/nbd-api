package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.WarningManagerStationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningManagerStation;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;
import com.neo.nbdapi.services.WarningMangerStationService;
import com.neo.nbdapi.services.objsearch.WarningManagerStationSearch;
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

            StringBuilder sql = new StringBuilder("select w.id, s.station_id, s.station_name, w.code, w.name, w.icon, w.created_at  from warning_manage_stations w inner join stations s on s.STATION_ID = w.STATION_ID where s.isdel = 0 and s.status = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {

                WarningManagerStationSearch objectSearch = objectMapper.readValue(search, WarningManagerStationSearch.class);
                if (objectSearch.getId() != null) {
                    sql.append(" AND w.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (objectSearch.getStationId() != null) {
                    sql.append(" AND s.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationName())) {
                    sql.append(" AND UPPER(s.station_name) like ? ");
                    paramSearch.add("%" + objectSearch.getStationName().toUpperCase()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getWarningCode())) {
                    sql.append(" AND UPPER(w.code) LIKE ? ");
                    paramSearch.add("%" + objectSearch.getWarningCode().toUpperCase()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getWarningName())) {
                    sql.append(" AND UPPER(w.name) like ? ");
                    paramSearch.add("%" + objectSearch.getWarningName().toUpperCase()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getIcon())) {
                    sql.append(" AND w.icon = ? ");
                    paramSearch.add(objectSearch.getIcon());
                }
                if (Strings.isNotEmpty(objectSearch.getStartDate())) {
                    sql.append(" and w.created_at > TO_DATE(?, 'dd/mm/yyyy') ");
                    paramSearch.add(objectSearch.getStartDate());
                }
                if (Strings.isNotEmpty(objectSearch.getEndDate())) {
                    sql.append(" and w.created_at < TO_DATE(?, 'dd/mm/yyyy') ");
                    paramSearch.add(objectSearch.getEndDate());
                }
            }
            sql.append(" ORDER BY w.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                WarningManagerStation warningManagerStation = WarningManagerStation.builder().
                        id(resultSetListData.getLong("id"))
                        .stationId(resultSetListData.getLong("station_id"))
                        .stationName(resultSetListData.getString("station_name"))
                        .warningCode(resultSetListData.getString("code"))
                        .warningName(resultSetListData.getString("name"))
                        .icon(resultSetListData.getString("icon"))
                        .createDate(resultSetListData.getDate("created_at"))
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
    public List<ComboBox> getListParameterWarningConfig(SelectWarningManagerVM selectVM) throws SQLException {
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
}
