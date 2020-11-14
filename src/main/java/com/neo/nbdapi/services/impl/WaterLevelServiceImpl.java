package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.WaterLevelDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.VariableTime;
import com.neo.nbdapi.entity.VariablesSpatial;
import com.neo.nbdapi.entity.WaterLevel;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;
import com.neo.nbdapi.services.WaterLevelService;
import com.neo.nbdapi.services.objsearch.WaterLevelSearch;
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
public class WaterLevelServiceImpl implements WaterLevelService {

    private Logger logger = LogManager.getLogger(ConfigValueTypeServiceImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private WaterLevelDAO waterLevelDAO;

    @Override
    public DefaultPaginationDTO getListWaterLevel(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<WaterLevel> waterLevels = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            logger.debug("mailConfigVM: {}", defaultRequestPagingVM);
            // start = pageNumber, lenght = recordPerPage
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select w.id, w.ts_id, w.value, w.timestamp, w.status, w.manual, w.warning, w.create_user  from water_Level w inner join station_time_series s on s.ts_id = w.ts_id where 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {
                WaterLevelSearch objectSearch = objectMapper.readValue(search, WaterLevelSearch.class);
                if (Strings.isNotEmpty(objectSearch.getStationId())) {
                    sql.append(" AND s.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (objectSearch.getId() != null) {
                    sql.append(" AND w.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (objectSearch.getTsId() != null) {
                    sql.append(" AND w.ts_id = ? ");
                    paramSearch.add(objectSearch.getTsId());
                }
                if (objectSearch.getValue()!=null) {
                    sql.append(" AND w.value = ? ");
                    paramSearch.add(objectSearch.getValue());
                }
                if (Strings.isNotEmpty(objectSearch.getStartDate())) {
                    sql.append(" AND w.timestamp  >=  to_timestamp(?, 'DD/MM/YYYY') ");
                    paramSearch.add(objectSearch.getStartDate());
                }
                if (Strings.isNotEmpty(objectSearch.getEndDate())) {
                    sql.append(" AND w.timestamp -1 <  to_timestamp(?, 'DD/MM/YYYY') ");
                    paramSearch.add(objectSearch.getEndDate());
                }
                if (objectSearch.getStatus() != null) {
                    sql.append(" AND w.status = ? ");
                    paramSearch.add(objectSearch.getStatus());
                }
                if (objectSearch.getManual() != null) {
                    sql.append(" AND w.manual= ? ");
                    paramSearch.add(objectSearch.getManual());
                }
                if (objectSearch.getWarning()!=null) {
                    sql.append(" AND w.warning = ? ");
                    paramSearch.add(objectSearch.getWarning());
                }
                if (Strings.isNotEmpty(objectSearch.getCreateUser())) {
                    sql.append(" and w.create_user like ? ");
                    paramSearch.add(objectSearch.getCreateUser());
                }
            }
            sql.append(" ORDER BY w.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                WaterLevel waterLevel = WaterLevel.builder().
                        id(resultSetListData.getLong("id"))
                        .tsId(resultSetListData.getLong("ts_id"))
                        .value(resultSetListData.getFloat("value"))
                        .timestamp(resultSetListData.getString("timestamp"))
                        .status(resultSetListData.getInt("status"))
                        .manual(resultSetListData.getInt("manual"))
                        .warning(resultSetListData.getInt("warning"))
                        .createUser(resultSetListData.getString("create_user"))
                        .build();

                waterLevels.add(waterLevel);
            }

            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(waterLevels.size())
                    .recordsTotal(total)
                    .content(waterLevels)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(waterLevels)
                    .build();
        }
    }

    @Override
    public DefaultResponseDTO updateWaterLevel(WaterLevelVM waterLevelVM) throws SQLException {
        List<Object> datas = waterLevelDAO.queryInformation(waterLevelVM);
        if(datas == null){
            return  DefaultResponseDTO.builder().status(-1).message("Lỗi lấy ra các thông số được cài đặt").build();
        }
        VariableTime variableTime = null;
        List<VariablesSpatial> variablesSpatials = null;
        Float nearest = null;

        variableTime = (VariableTime) datas.get(0);

        variablesSpatials = (List<VariablesSpatial>) datas.get(1);

        nearest = (Float) datas.get(2);
        Boolean continude = false;
        if(waterLevelVM.getValue() < variableTime.getMin()){
            waterLevelVM.setWarning(2);
            continude = true;
        }

        if(waterLevelVM.getValue() > variableTime.getMax() && !continude){
            waterLevelVM.setWarning(3);
            continude = true;
            //update và return
        }

        if(nearest!=null){
            if(Math.abs(waterLevelVM.getValue() - nearest) > variableTime.getVariableTime() && !continude){
                waterLevelVM.setWarning(4);
                continude = true;
            }
        }

        if(!continude){
            for(VariablesSpatial variablesSpatial : variablesSpatials){
                if(waterLevelVM.getValue() -variablesSpatial.getMin() > variablesSpatial.getVariableSpatial()){
                    waterLevelVM.setWarning(5);
                    break;
                } else if(waterLevelVM.getValue() -variablesSpatial.getMax() > variablesSpatial.getVariableSpatial()){
                    waterLevelVM.setWarning(5);
                    break;
                }
            }
        }
        return waterLevelDAO.updateWaterLevel(waterLevelVM);
    }

    @Override
    public List<WaterLevel> getListWaterLevelByTime(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException, BusinessException {
        //List<WaterLevel> list = waterLevelDAO.getListWaterLevelByTime(defaultRequestPagingVM);
        return null;
    }
}
