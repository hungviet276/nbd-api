package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.ConfigValueTypeDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
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

    @Autowired
    private ConfigValueTypeDAO configValueTypeDAO;


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

            StringBuilder sql = new StringBuilder("select c.id, c.station_id, c.PARAMETER_TYPE_ID,s.station_name, v.parameter_type_name, c.min, c.max, c.variable_time,c.code, c.variable_spatial, c.start_apply_date, c.end_apply_date" +
                    " from config_value_types c inner join stations s on s.station_id = c.station_id " +
                    " inner join parameter_type  v on v.parameter_type_id = c.PARAMETER_TYPE_ID where 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {

                ConfigValueTypeSearch objectSearch = objectMapper.readValue(search, ConfigValueTypeSearch.class);
                if (objectSearch.getId() != null) {
                    sql.append(" AND c.id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationId())) {
                    sql.append(" AND c.station_id = ? ");
                    paramSearch.add(objectSearch.getStationId());
                }
                if (Strings.isNotEmpty(objectSearch.getCode())) {
                    sql.append(" AND UPPER(c.code) LIKE ? ");
                    paramSearch.add("%"+objectSearch.getCode().toUpperCase()+"%");
                }
                if (objectSearch.getValueTypeId() != null) {
                    sql.append(" AND c.PARAMETER_TYPE_ID = ? ");
                    paramSearch.add(objectSearch.getValueTypeId());
                }
                if (Strings.isNotEmpty(objectSearch.getStationName())) {
                    sql.append(" AND UPPER(s.station_name) LIKE ? ");
                    paramSearch.add("%" + objectSearch.getStationName().toUpperCase()+ "%");
                }
                if (Strings.isNotEmpty(objectSearch.getValueTypename())) {
                    sql.append(" AND UPPER(v.parameter_type_name) like ? ");
                    paramSearch.add("%" + objectSearch.getValueTypename().toUpperCase()+ "%");
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
                    sql.append(" AND trunc(c.start_apply_date) >= trunc(to_date(?,'DD/MM/YYYY')) ");
                    paramSearch.add(objectSearch.getStartDate());
                }
                if (Strings.isNotEmpty(objectSearch.getEndDate())) {
                    sql.append(" AND trunc(c.end_apply_date) <= trunc(to_date(?,'DD/MM/YYYY')) ");
                    paramSearch.add(objectSearch.getEndDate());
                }
            }
            sql.append(" ORDER BY c.id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                ConfigValueType configValueType = ConfigValueType.builder()
                        .id(resultSetListData.getLong("id"))
                        .stationId(resultSetListData.getString("station_id"))
                        .valueTypeId(resultSetListData.getLong("parameter_type_id"))
                        .stationName(resultSetListData.getString("station_name"))
                        .valueTypename(resultSetListData.getString("parameter_type_name"))
                        .min(resultSetListData.getFloat("min"))
                        .max(resultSetListData.getFloat("max"))
                        .variableTime(resultSetListData.getFloat("variable_time"))
                        .variableSpatial(resultSetListData.getFloat("variable_spatial"))
                        .startDate(resultSetListData.getDate("start_apply_date"))
                        .endDate(resultSetListData.getDate("end_apply_date"))
                        .code(resultSetListData.getString("code"))
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

    @Override
    public List<ComboBox> getValueType(String stationId, Long valueTypeId) throws SQLException {
        return configValueTypeDAO.getValueType(stationId,valueTypeId);
    }

    @Override
    public List<ComboBox> getStationComboBox(String query) throws SQLException {
        return configValueTypeDAO.getStationComboBox(query);
    }

    @Override
    public List<ComboBoxStr> getStationComboBox(String query, String idStation) throws SQLException {
        return configValueTypeDAO.getStationComboBox(query,idStation);
    }

    @Override
    public StationValueTypeSpatialDTO getStationValueTypeSpatial(String idStation, Long idValueType, String code) throws SQLException {
        return configValueTypeDAO.getStationValueTypeSpatial(idStation, idValueType, code);
    }

    @Override
    public DefaultResponseDTO createConfigValuetype(ConfigValueTypeDTO configValueTypeDTO) throws Exception {
        boolean isInsert = configValueTypeDAO.isInsert(configValueTypeDTO);
        if(!isInsert){
            return DefaultResponseDTO.builder().status(0).message("Dữ liệu bị chồng lấn khoảng thời gian").build();
        }
        return configValueTypeDAO.createConfigValuetype(configValueTypeDTO);
    }

    @Override
    public List<StationValueTypeSpatialDTO> getStationValueTypeSpatials(Long idConfigValueTypeParent) throws SQLException {
        return configValueTypeDAO.getStationValueTypeSpatials(idConfigValueTypeParent);
    }

    @Override
    public DefaultResponseDTO editConfigValuetype(ConfigValueTypeDTO configValueTypeDTO) throws Exception {
        boolean isInsert = configValueTypeDAO.isInsert(configValueTypeDTO);
        if(!isInsert){
            return DefaultResponseDTO.builder().status(0).message("Dữ liệu bị chồng lấn khoảng thời gian").build();
        }
        //láy ra danh sách config đã tồn tại về không gian
        List<ConfigStationsCommrelateDTO> configStationsCommrelateDTOTmps = configValueTypeDAO.getListConfigStationsCommrelateDTO(configValueTypeDTO.getId());
        // chia ra làm 2 danh sách 1 danh sách để xóa, 1 danh sách để thêm mới

        List<ConfigStationsCommrelateDTO> deletes = new ArrayList<>();

        List<ConfigStationsCommrelateDTO> creates = new ArrayList<>();

        Long[] idViews = configValueTypeDTO.getStationSpatial();

        // thêm danh sách xóa

        for (Long tmp: idViews) {
            boolean add = false;
            for (ConfigStationsCommrelateDTO configStationsCommrelateDTO : configStationsCommrelateDTOTmps) {
                if(configStationsCommrelateDTO.getConfigValueTypeParent().equals(configValueTypeDTO.getId())&& tmp.equals(configStationsCommrelateDTO.getConfigValueTypeId()))
                    add = true;
            }
            if(!add){
                ConfigStationsCommrelateDTO addTmp = new ConfigStationsCommrelateDTO();
                addTmp.setConfigValueTypeParent(configValueTypeDTO.getId());
                addTmp.setConfigValueTypeId(tmp);
                creates.add(addTmp);
            }
        }
        // thêm danh sách thêm mới

        for (ConfigStationsCommrelateDTO configStationsCommrelateDTO: configStationsCommrelateDTOTmps) {
            boolean delete = false;
            for (Long tmp: idViews) {
                if(configStationsCommrelateDTO.getConfigValueTypeParent().equals(configValueTypeDTO.getId())&& tmp.equals(configStationsCommrelateDTO.getConfigValueTypeId())){
                    delete = true;
                }
            }
            if(!delete){
                deletes.add(configStationsCommrelateDTO);
            }
        }
        // thực hiện cập nhật
        return configValueTypeDAO.editConfigValuetype(configValueTypeDTO,deletes, creates );
    }

    @Override
    public DefaultResponseDTO deleteConfigValuetype(Long id) throws Exception {
        return configValueTypeDAO.deleteConfigValuetype(id);
    }
}
