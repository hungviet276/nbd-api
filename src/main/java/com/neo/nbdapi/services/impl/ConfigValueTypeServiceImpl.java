package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ConfigValueType;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.ConfigValueTypeService;
import com.neo.nbdapi.services.objsearch.ConfigValueTypeSearch;
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
public class ConfigValueTypeServiceImpl implements ConfigValueTypeService {

    private Logger logger = LogManager.getLogger(ConfigValueTypeServiceImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;


    @Override
    public DefaultPaginationDTO getListConfigValueType(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<ConfigValueType> configValueTypes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            logger.debug("mailConfigVM: {}", defaultRequestPagingVM);
            // start = pageNumber, lenght = recordPerPage
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select c.id, c.station_id, c.value_type_id,s.station_name, v.value_type_name, c.min, c.max, c.variable_time, c.variable_spatial, c.start_apply_date, c.end_apply_date \n" +
                    "from config_value_types c inner join stations s on s.station_id = c.station_id " +
                    "inner join value_types  v on v.value_type_id = c.value_type_id where 1 = 1");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {

                ConfigValueTypeSearch objectSearch = objectMapper.readValue(search, ConfigValueTypeSearch.class);
                if (objectSearch.getId() != null) {
                    sql.append(" AND c.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (objectSearch.getStationId() != null) {
                    sql.append(" AND c.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (objectSearch.getValueTypeId() != null) {
                    sql.append(" AND c.value_type_id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationName())) {
                    sql.append(" AND s.station_name LIKE ? ");
                    paramSearch.add("%" + objectSearch.getStationName()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getValueTypename())) {
                    sql.append(" AND v.value_type_name like ? ");
                    paramSearch.add("%" + objectSearch.getValueTypename()+ "%");
                }
                if (objectSearch.getMin() != null) {
                    sql.append(" AND c.min = ? ");
                    paramSearch.add(objectSearch.getMin());
                }
                if (objectSearch.getMax() != null) {
                    sql.append(" AND c.max = ? ");
                    paramSearch.add(objectSearch.getMax());
                }
                if (objectSearch.getVariableTime() != null) {
                    sql.append(" AND c.variable_time = ? ");
                    paramSearch.add(objectSearch.getVariableTime());
                }
                if (objectSearch.getVariableSpatial() != null) {
                    sql.append(" AND c.variable_spatial = ? ");
                    paramSearch.add(objectSearch.getVariableSpatial());
                }
                if (Strings.isNotEmpty(objectSearch.getStartDate() )) {
                    sql.append(" AND c.start_apply_date >= to_date(?,'DD/MM/YYYY') ");
                    paramSearch.add(objectSearch.getStartDate());
                }
                if (Strings.isNotEmpty(objectSearch.getEndDate())) {
                    sql.append(" AND c.end_apply_date <= to_date(?,'DD/MM/YYYY') ");
                    paramSearch.add(objectSearch.getEndDate());
                }
            }
            sql.append(" ORDER BY c.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                ConfigValueType configValueType = ConfigValueType.builder()
                        .id(resultSetListData.getLong("id"))
                        .stationId(resultSetListData.getLong("station_id"))
                        .valueTypeId(resultSetListData.getLong("value_type_id"))
                        .stationName(resultSetListData.getString("station_name"))
                        .valueTypename(resultSetListData.getString("value_type_name"))
                        .min(resultSetListData.getFloat("min"))
                        .max(resultSetListData.getFloat("max"))
                        .variableTime(resultSetListData.getFloat("variable_time"))
                        .variableSpatial(resultSetListData.getFloat("variable_spatial"))
                        .startDate(resultSetListData.getDate("start_apply_date"))
                        .endDate(resultSetListData.getDate("end_apply_date"))
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
