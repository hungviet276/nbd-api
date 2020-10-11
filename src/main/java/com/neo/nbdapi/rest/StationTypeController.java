package com.neo.nbdapi.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.Parameter;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.StationManagementService;
import com.neo.nbdapi.services.impl.MailConfigServiceImpl;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/station-type")
public class StationTypeController {
    private Logger logger = LogManager.getLogger(StationTypeController.class);

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;
    
    @Autowired
    private StationManagementService stationManagementService;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @GetMapping("/get-list-object-type")
    public List<ComboBox> getListObjectType() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from OBJECT_TYPE WHERE 1 = 1 ");
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
                        .id(rs.getLong("OBJECT_TYPE_ID"))
                        .text(rs.getString("OBJECT_TYPE") + " - " + rs.getString("OBJECT_TYPE_SHORTNAME"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }

    @PostMapping("/get-list-station-time-series-pagination")
    public DefaultPaginationDTO getListStationTimeSeriesPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*,b.status, e.UNIT_NAME from station_time_series a,stations b, stations_object_type c, PARAMETER_TYPE d, unit e\r\n" + 
            		" where 1=1 and a.station_id = b.station_id(+) and b.station_id = c.stations_id(+) and a.PARAMETERTYPE_ID = d.PARAMETER_TYPE_ID(+) and d.UNIT_ID = e.UNIT_ID ");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                	Map<String,String> params = objectMapper.readValue(search, Map.class);
                	if (Strings.isNotEmpty(params.get("s_tsName"))) {
                        sql.append(" and a.TS_NAME = ? ");
                        paramSearch.add(params.get("s_tsName"));
                    }
                    if (Strings.isNotEmpty(params.get("s_stationCode"))) {
                        sql.append(" and b.station_code = ? ");
                        paramSearch.add(params.get("s_stationCode"));
                    }
                    if (Strings.isNotEmpty(params.get("s_stationName"))) {
                        sql.append(" and a.STATION_NAME like ? ");
                        paramSearch.add("%" + params.get("s_stationName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_stationLong"))) {
                        sql.append(" and a.STATION_LONGTITUDE = ? ");
                        paramSearch.add(params.get("s_stationLong"));
                    }
                    if (Strings.isNotEmpty(params.get("s_stationLat"))) {
                        sql.append(" and a.STATION_LATITUDE = ? ");
                        paramSearch.add(params.get("s_stationLat"));
                    }
                    if (Strings.isNotEmpty(params.get("s_provinceName"))) {
                        sql.append(" and a.PROVINCE_NAME = ? ");
                        paramSearch.add("%" + params.get("s_provinceName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_districtName"))) {
                        sql.append(" and a.DISTRICT_NAME = ? ");
                        paramSearch.add("%" + params.get("s_districtName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_wardName"))) {
                        sql.append(" and a.WARD_NAME = ? ");
                        paramSearch.add("%" + params.get("s_wardName") + "%");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<StationTimeSeries> list = new ArrayList<>();

            while (rs.next()) {
                StationTimeSeries bo = StationTimeSeries.builder()
                        .tsId(rs.getInt("TS_ID"))
                        .tsName(rs.getString("TS_NAME"))
                        .stationId(rs.getInt("STATION_ID"))
                        .tsTypeId(rs.getInt("TS_TYPE_ID"))
                        .parameterTypeId(rs.getInt("PARAMETERTYPE_ID"))
                        .parameterTypeName(rs.getString("PARAMETERTYPE_NAME"))
                        .parameterTypeDescription(rs.getString("PARAMETERTYPE_DESCRIPTION"))
                        .stationNo(rs.getString("STATION_NO"))
                        .stationName(rs.getString("STATION_NAME"))
                        .stationLongName(rs.getString("STATION_LONGNAME"))
                        .stationLat(rs.getFloat("STATION_LATITUDE"))
                        .stationLong(rs.getFloat("STATION_LONGTITUDE"))
                        .catchmentId(rs.getInt("CATCHMENT_ID"))
                        .catchmentName(rs.getString("CATCHMENT_NAME"))
                        .siteId(rs.getInt("SITE_ID"))
                        .siteName(rs.getString("SITE_NAME"))
                        .riverId(rs.getInt("RIVER_ID"))
                        .riverName(rs.getString("RIVER_NAME"))
                        .provinceId(rs.getInt("PROVINCE_ID"))
                        .provinceName(rs.getString("PROVINCE_NAME"))
                        .districtId(rs.getInt("DISTRICT_ID"))
                        .districtName(rs.getString("DISTRICT_NAME"))
                        .countryId(rs.getInt("COUNTRY_ID"))
                        .countryName(rs.getString("COUNTRY_NAME"))
                        .wardId(rs.getInt("WARD_ID"))
                        .wardName(rs.getString("WARD_NAME"))
                        .projectId(rs.getInt("PROJECT_ID"))
                        .projectName(rs.getString("PROJECT_NAME"))
                        .storage(rs.getString("STORAGE"))
                        .status(rs.getInt("STATUS"))
                        .unitName(rs.getString("UNIT_NAME"))
                        .build();
                list.add(bo);
            }

            rs.close();
            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(list.size())
                    .recordsTotal(total)
                    .content(list)
                    .build();
        }
    }
    
    @PostMapping("/create-parameter-type")
    public DefaultResponseDTO createParameterType(@RequestBody @Valid Map<String,String> params) throws SQLException {
    	String sql = "insert into PARAMETER_TYPE(PARAMETER_TYPE_ID, PARAMETER_TYPE_NAME, PARAMETER_TYPE_DESCRIPTION, UNIT_ID) values (PARAMETER_TYPE_SEQ.nextval,?,?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, params.get("parameter"));
            statement.setString(2, params.get("parameterDes"));
            statement.setString(3, params.get("unit"));
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        }
    }
    
    @PostMapping("/create-parameter")
    public DefaultResponseDTO createParameter(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
    	log.info(objectMapper.writeValueAsString(params));
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "insert into PARAMETER_KTTV(STATION_PARAMETER_ID, PARAMETER_TYPE_ID, UUID, TIME_FREQUENCY) values (PARAMETER_KTTV_SEQ.nextval,?,?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, params.get("parameter"));
            statement.setString(2, params.get("uuid"));
            statement.setString(3, params.get("frequency"));
            statement.execute();
            
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Thêm mới thất bại: "+ e.getMessage());
            return defaultResponseDTO;
		}
    }
    
    @PostMapping("/get-list-station-parameter-pagination")
    public DefaultPaginationDTO getListStationParameterPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*,b.PARAMETER_TYPE_NAME, c.UNIT_NAME from PARAMETER_KTTV a, PARAMETER_TYPE b, unit c where a.PARAMETER_TYPE_ID = b.PARAMETER_TYPE_ID(+) and b.UNIT_ID = c.UNIT_ID(+) ");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                	Map<String,String> params = objectMapper.readValue(search, Map.class);
                	if (Strings.isNotEmpty(params.get("s_uuid"))) {
                        sql.append(" and uuid = ? ");
                        paramSearch.add(params.get("s_uuid"));
                    }
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.debug("SQL get-list-station-parameter-pagination : {}", sql.toString());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<Parameter> list = new ArrayList<>();

            while (rs.next()) {
            	Parameter bo = Parameter.builder()
                        .stationParamterId(rs.getInt("STATION_PARAMETER_ID"))
                        .paramterTypeId(rs.getInt("PARAMETER_TYPE_ID"))
                        .parameterName(rs.getString("PARAMETER_TYPE_NAME"))
                        .stationId(rs.getInt("STATION_ID"))
                        .timeFrequency(rs.getInt("TIME_FREQUENCY"))
                        .uuid(rs.getString("UUID"))
                        .unitName(rs.getString("UNIT_NAME"))
                        .note("")
                        .build();
                list.add(bo);
            }

            rs.close();
            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(list.size())
                    .recordsTotal(total)
                    .content(list)
                    .build();
        }
    }
    
    @PostMapping("/create-object-type")
    public DefaultResponseDTO createStationType(@RequestBody @Valid Map<String,String> params) throws SQLException {
    	String sql = "INSERT INTO OBJECT_TYPE(OBJECT_TYPE_ID, OBJECT_TYPE, OBJECT_TYPE_SHORTNAME) values (OBJECT_TYPE_SEQ.nextval, ?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, params.get(""));
            statement.setString(2, params.get(""));
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        }
    }

    @PostMapping("/create-station-time-series")
    public DefaultResponseDTO createStationTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
    	DefaultResponseDTO defaultResponseDTO = stationManagementService.createStationTimeSeriesPLSQL(params);
    	return defaultResponseDTO;
    }
    
    @PostMapping("/update-station-time-series")
    public DefaultResponseDTO updateStationTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException {
    	String sql = "update STATION_TIME_SERIES set TS_ID = ?, TS_NAME = ?, STATION_ID = ?, TS_TYPE_ID = ?, PARAMETERTYPE_ID =?, PARAMETERTYPE_NAME =?, "
        		+ "PARAMETERTYPE_DESCRIPTION = ?, STATION_NO = ?, STATION_NAME = ?, STATION_LONGNAME = ?, STATION_LATITUDE = ?, STATION_LONGTITUDE = ?, CATCHMENT_ID = ?, "
        		+ "CATCHMENT_NAME = ?, SITE_ID = ?, SITE_NAME = ?, RIVER_ID = ?, RIVER_NAME = ?, PROVINCE_ID = ?, PROVINCE_NAME = ?, DISTRICT_ID = ?, DISTRICT_NAME = ?, WARD_ID = ?, "
        		+ "WARD_NAME = ?, COUNTRY_ID = ?, COUNTRY_NAME = ?, PROJECT_ID = ?, PROJECT_NAME = ?, STORAGE = ?, UPDATE_DATE = sysdate "
        		+ " where ID = ?";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql)) {
            int idx = 1;
            statement.setString(idx++, params.get("TS_ID"));
            statement.setString(idx++, params.get("TS_NAME"));
            statement.setString(idx++, params.get("STATION_ID"));
            statement.setString(idx++, params.get("TS_TYPE_ID"));
            statement.setString(idx++, params.get("PARAMETERTYPE_ID"));
            statement.setString(idx++, params.get("PARAMETERTYPE_NAME"));
            statement.setString(idx++, params.get("PARAMETERTYPE_DESCRIPTION"));
            statement.setString(idx++, params.get("STATION_NO"));
            statement.setString(idx++, params.get("STATION_NAME"));
            statement.setString(idx++, params.get("STATION_LONGNAME"));
            statement.setString(idx++, params.get("STATION_LATITUDE"));
            statement.setString(idx++, params.get("STATION_LONGTITUDE"));
            statement.setString(idx++, params.get("CATCHMENT_ID"));
            statement.setString(idx++, params.get("CATCHMENT_NAME"));
            statement.setString(idx++, params.get("SITE_ID"));
            statement.setString(idx++, params.get("SITE_NAME"));
            statement.setString(idx++, params.get("RIVER_ID"));
            statement.setString(idx++, params.get("RIVER_NAME"));
            statement.setString(idx++, params.get("PROVINCE_ID"));
            statement.setString(idx++, params.get("PROVINCE_NAME"));
            statement.setString(idx++, params.get("DISTRICT_ID"));
            statement.setString(idx++, params.get("DISTRICT_NAME"));
            statement.setString(idx++, params.get("WARD_ID"));
            statement.setString(idx++, params.get("WARD_NAME"));
            statement.setString(idx++, params.get("COUNTRY_ID"));
            statement.setString(idx++, params.get("COUNTRY_NAME"));
            statement.setString(idx++, params.get("PROJECT_ID"));
            statement.setString(idx++, params.get("PROJECT_NAME"));
            statement.setString(idx++, params.get("STORAGE"));
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Cập nhật thành công");
            return defaultResponseDTO;
        }
    }
    
    @PostMapping("/delete-station-time-series")
    public DefaultResponseDTO deleteStationTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException {
    	String sql = "delete STATION_TIME_SERIES where ID = ?";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            int idx = 1;
            statement.setString(idx++, params.get("ID"));
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
            return defaultResponseDTO;
        }
    }
}
