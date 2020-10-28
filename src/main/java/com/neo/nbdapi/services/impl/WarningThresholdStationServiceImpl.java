package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.WarningThresholdStation;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.WarningThresholdStationService;
import com.neo.nbdapi.services.objsearch.WarningThresholdStationSearch;
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
public class WarningThresholdStationServiceImpl implements WarningThresholdStationService {

    private Logger logger = LogManager.getLogger(ConfigValueTypeServiceImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Override
    public DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<WarningThresholdStation> configValueTypes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            logger.debug("mailConfigVM: {}", defaultRequestPagingVM);
            // start = pageNumber, lenght = recordPerPage
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select w.id,s.station_id, w.parameter_type_id,s.station_name, p.parameter_type_name, w.value_level1, w.value_level2, w.value_level3, w.value_level4, w.value_level5, w.threshold_code from warning_threshold_value w " +
                    "inner join stations s on w.station_id =  s.station_id inner join parameter_type p on p.parameter_type_id = w.parameter_type_id inner join warning_threshold wt on wt.code = w.threshold_code where 1 = 1 and s.isdel = 0 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {

                WarningThresholdStationSearch objectSearch = objectMapper.readValue(search, WarningThresholdStationSearch.class);
                if (objectSearch.getId() != null) {
                    sql.append(" AND w.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (objectSearch.getStationId() != null) {
                    sql.append(" AND s.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (objectSearch.getParameterId()!=null) {
                    sql.append(" AND w.parameter_type_id = ? ");
                    paramSearch.add(objectSearch.getParameterId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationName())) {
                    sql.append(" AND UPPER(s.station_name) LIKE ? ");
                    paramSearch.add("%" + objectSearch.getStationName().toUpperCase()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getParameterName())) {
                    sql.append(" AND UPPER(p.parameter_type_name) like ? ");
                    paramSearch.add("%" + objectSearch.getParameterName().toUpperCase()+ "%");
                }
                if (objectSearch.getValueLevel1() != null) {
                    sql.append(" AND w.value_level1 = ? ");
                    paramSearch.add(objectSearch.getValueLevel1());
                }
                if (objectSearch.getValueLevel2() != null) {
                    sql.append(" AND w.value_level2 = ? ");
                    paramSearch.add(objectSearch.getValueLevel2());
                }
                if (objectSearch.getValueLevel3() != null) {
                    sql.append(" AND w.value_level3 = ? ");
                    paramSearch.add(objectSearch.getValueLevel3());
                }
                if (objectSearch.getValueLevel4() != null) {
                    sql.append(" AND w.value_level4 = ? ");
                    paramSearch.add(objectSearch.getValueLevel4());
                }
                if (objectSearch.getValueLevel5() != null) {
                    sql.append(" AND w.value_level5 = ? ");
                    paramSearch.add(objectSearch.getValueLevel5());
                }
                if (Strings.isNotEmpty(objectSearch.getThresholdCode())) {
                    sql.append(" AND UPPER(w.threshold_code) like ? ");
                    paramSearch.add("%"+(objectSearch.getThresholdCode().toUpperCase())+"%");
                }

            }
            sql.append(" ORDER BY w.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                WarningThresholdStation configValueType = WarningThresholdStation.builder()
                        .id(resultSetListData.getLong("id"))
                        .stationId(resultSetListData.getLong("station_id"))
                        .parameterId(resultSetListData.getLong("parameter_type_id"))
                        .stationName(resultSetListData.getString("station_name"))
                        .parameterName(resultSetListData.getString("parameter_type_name"))
                        .valueLevel1(resultSetListData.getFloat("value_level1"))
                        .valueLevel2(resultSetListData.getFloat("value_level2"))
                        .valueLevel3(resultSetListData.getFloat("value_level3"))
                        .valueLevel4(resultSetListData.getFloat("value_level4"))
                        .valueLevel5(resultSetListData.getFloat("value_level5"))
                        .thresholdCode(resultSetListData.getString("threshold_code"))
                        .build();
                configValueTypes.add(configValueType);
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
}
