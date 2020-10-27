package com.neo.nbdapi.rest;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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
import com.neo.nbdapi.entity.Station;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.StationManagementService;
import com.neo.nbdapi.services.impl.MailConfigServiceImpl;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.HtmlUtils;

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

    @PostMapping("/get-list-station-pagination")
    public DefaultPaginationDTO getListStationPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*, b.PROVINCE_NAME, c.DISTRICT_NAME, e.RIVER_NAME, d.WARD_NAME, g.* from stations a , PROVINCES b, DISTRICTS c, WARDS d, rivers e , stations_object_type f , OBJECT_TYPE g\r\n" + 
            		"where a.PROVINCE_ID = b.PROVINCE_ID(+) and a.DISTRICT_ID = c.DISTRICT_ID(+) and a.WARD_ID = d.WARD_ID(+) and a.RIVER_ID = e.RIVER_ID(+) and a.STATION_ID = f.STATION_ID and f.OBJECT_TYPE_ID = g.OBJECT_TYPE_ID");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                	Map<String,String> params = objectMapper.readValue(search, Map.class);
                	if (Strings.isNotEmpty(params.get("s_objectType"))) {
                        sql.append(" and g.OBJECT_TYPE = ? ");
                        paramSearch.add(params.get("s_objectType"));
                    }
                	if (Strings.isNotEmpty(params.get("s_objectTypeName"))) {
                        sql.append(" and lower(g.OBJECT_TYPE_SHORTNAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_objectTypeName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_stationCode"))) {
                        sql.append(" and a.station_code = ? ");
                        paramSearch.add(params.get("s_stationCode"));
                    }
                    if (Strings.isNotEmpty(params.get("s_stationName"))) {
                        sql.append(" and lower(a.STATION_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_stationName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_longtitude"))) {
                        sql.append(" and a.LONGTITUDE = ? ");
                        paramSearch.add(params.get("s_longtitude"));
                    }
                    if (Strings.isNotEmpty(params.get("s_latitude"))) {
                        sql.append(" and a.LATITUDE = ? ");
                        paramSearch.add(params.get("s_latitude"));
                    }
                    if (Strings.isNotEmpty(params.get("s_provinceName"))) {
                        sql.append(" and lower(b.PROVINCE_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_provinceName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_districtName"))) {
                        sql.append(" and lower(c.DISTRICT_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_districtName") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_wardName"))) {
                        sql.append(" and lower(d.WARD_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_wardName") + "%");
                    }
                    
                    if (Strings.isNotEmpty(params.get("s_address"))) {
                        sql.append(" and lower(a.address) like lower(?) ");
                        paramSearch.add("%" + params.get("s_address") + "%");
                    }

                    if (Strings.isNotEmpty(params.get("s_riverName"))) {
                        sql.append(" and lower(e.RIVER_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_riverName") + "%");
                    }

                    if (Strings.isNotEmpty(params.get("s_status"))) {
                        sql.append(" and a.status = ? ");
                        paramSearch.add(params.get("s_status"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info(sql.toString());
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<Station> list = new ArrayList<>();

            while (rs.next()) {
                Station bo = Station.builder()
                        .stationId(rs.getLong("STATION_ID"))
                        .stationCode(rs.getString("STATION_CODE"))
                        .elevation(rs.getFloat("ELEVATION"))
                        .stationName(rs.getString("STATION_NAME"))
                        .latitude(rs.getFloat("LATITUDE"))
                        .longtitude(rs.getFloat("LONGTITUDE"))
                        .trans_miss(rs.getInt("TRANS_MISS"))
                        .address(rs.getString("ADDRESS"))
                        .status(rs.getInt("STATUS"))
                        .riverId(rs.getLong("RIVER_ID"))
                        .riverName(rs.getString("RIVER_NAME"))
                        .provinceId(rs.getLong("PROVINCE_ID"))
                        .provinceName(rs.getString("PROVINCE_NAME"))
                        .districtId(rs.getLong("DISTRICT_ID"))
                        .districtName(rs.getString("DISTRICT_NAME"))
                        .countryId(rs.getInt("COUNTRY_ID"))
//                        .countryName(rs.getString("COUNTRY_NAME"))
                        .wardId(rs.getLong("WARD_ID"))
                        .wardName(rs.getString("WARD_NAME"))
                        .projectId(rs.getInt("PROJECT_ID"))
                        .modeStationType(rs.getInt("MODE_CONTROL"))
                        .areaId(rs.getLong("AREA_ID"))
                        .stationTypeId(rs.getLong("STATION_TYPE_ID"))
                        .objectTypeId(rs.getInt("OBJECT_TYPE_ID"))
                        .objectType(rs.getString("OBJECT_TYPE"))
                        .objectTypeName(rs.getString("OBJECT_TYPE_SHORTNAME"))
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
    
    @PostMapping("/get-list-station-time-series-pagination")
    public DefaultPaginationDTO getListStationTimeSeriesPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*,b.status, e.UNIT_NAME from station_time_series a,stations b, stations_object_type c, PARAMETER_TYPE d, unit e\r\n" + 
            		" where 1=1 and a.station_id = b.station_id(+) and b.station_id = c.station_id(+) and a.PARAMETERTYPE_ID = d.PARAMETER_TYPE_ID(+) and d.UNIT_ID = e.UNIT_ID ");
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
            log.info(sql.toString());
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

    @PostMapping("/search-parameter-type")
    public DefaultPaginationDTO searchParameterType(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select a.*, b.listSeries,c.unit_name from parameter_type a ,\n" +
                "  (select c.PARAMETER_TYPE_ID , LISTAGG(TS_TYPE_NAME, '; ') \n" +
                "                    WITHIN GROUP (ORDER BY TS_TYPE_NAME) listSeries  from (\n" +
                "select a.PARAMETER_TYPE_ID, b.TS_TYPE_NAME from TIME_SERIES_CONFIG a, TIME_SERIES_TYPE b where a.TS_TYPE_ID = b.TS_TYPE_ID \n" +
                ") c where 1=1 GROUP BY PARAMETER_TYPE_ID) b, unit c\n" +
                "    where a.PARAMETER_TYPE_ID = b.PARAMETER_TYPE_ID and a.unit_id = c.unit_id");
        try (Connection connection = ds.getConnection();) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    Map<String,String> params = objectMapper.readValue(search, Map.class);
                    if (Strings.isNotEmpty(params.get("s_parameter"))) {
                        sql.append(" and lower(a.PARAMETER_TYPE_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_parameter") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_parameterDesc"))) {
                        sql.append(" and lower(a.PARAMETER_TYPE_DESCRIPTION) like lower(?) ");
                        paramSearch.add("%" +params.get("s_parameterDesc")+ "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_unit"))) {
                        sql.append(" and lower(c.UNIT_NAME) like lower(?) ");
                        paramSearch.add("%" + params.get("s_unit") + "%");
                    }
                    if (Strings.isNotEmpty(params.get("s_timeseries"))) {
                        sql.append(" and lower(b.LISTSERIES) like lower(?) ");
                        paramSearch.add("%" + params.get("s_timeseries") + "%");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info(sql.toString());
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<StationTimeSeries> list = new ArrayList<>();

            while (rs.next()) {
                StationTimeSeries bo = StationTimeSeries.builder()
                        .parameterTypeId(rs.getInt("PARAMETER_TYPE_ID"))
                        .parameterTypeName(rs.getString("PARAMETER_TYPE_NAME"))
                        .parameterTypeDescription(rs.getString("PARAMETER_TYPE_DESCRIPTION"))
                        .unitId(Integer.parseInt(rs.getString("UNIT_ID")))
                        .unitName(rs.getString("UNIT_NAME"))
                        .timeSeries(rs.getString("LISTSERIES"))
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

    @PostMapping("/create-time-series-config")
    public DefaultResponseDTO createTimeSeriesConfig(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
        log.info(objectMapper.writeValueAsString(params));
        for(Map.Entry entry : params.entrySet()){
            entry.setValue(HtmlUtils.htmlEscape(entry.getKey().toString()));
        }
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "insert into TIME_SERIES_CONFIG(TS_CONFIG_ID,TS_CONFIG_NAME,TS_TYPE_ID, UUID) values (TIME_SERIES_CONFIG_SEQ.nextval,?,?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
            statement.setString(i++, params.get("tsConfigName"));
            statement.setString(i++, params.get("timeTypeId"));
            statement.setString(i++, params.get("uuid"));
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

    @PostMapping("/create-parameter-type")
    public DefaultResponseDTO createParameterType(@RequestBody @Valid Map<String,String> params) throws SQLException {
        for(Map.Entry entry : params.entrySet()){
            entry.setValue(HtmlUtils.htmlEscape(entry.getKey().toString()));
        }
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
        for(Map.Entry entry : params.entrySet()){
            entry.setValue(HtmlUtils.htmlEscape(entry.getKey().toString()));
        }
        log.info(objectMapper.writeValueAsString(params));
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "insert into PARAMETER(STATION_PARAMETER_ID, PARAMETER_TYPE_ID, UUID, TIME_FREQUENCY,STATION_ID) values (PARAMETER_KTTV_SEQ.nextval,?,?,?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
        	statement.setString(i++, params.get("parameter"));
            statement.setString(i++, params.get("uuid"));
            statement.setString(i++, params.get("frequency"));
            statement.setString(i++, params.get("stationId"));
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
    
    @PostMapping("/delete-parameter")
    public DefaultResponseDTO deleteParameter(@RequestParam(name="stationParamterId") String stationParamterId) throws SQLException, JsonProcessingException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "delete from PARAMETER where STATION_PARAMETER_ID = ? ";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, stationParamterId);
            statement.execute();
            
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
            return defaultResponseDTO;
        }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Xóa thất bại: "+ e.getMessage());
            return defaultResponseDTO;
		}
    }

    @PostMapping("/delete-time-series")
    public DefaultResponseDTO deleteTimeSeries(@RequestParam(name="stationParamterId") String stationParamterId) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "delete from TIME_SERIES where TS_ID = ? ";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, stationParamterId);
            statement.execute();

            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
            return defaultResponseDTO;
        }catch (Exception e) {
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Xóa thất bại: "+ e.getMessage());
            return defaultResponseDTO;
        }
    }

    @PostMapping("/delete-time-series-config")
    public DefaultResponseDTO deleteTimeSeriesConfig(@RequestParam(name="stationParamterId") String stationParamterId) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "delete from TIME_SERIES_CONFIG where TS_CONFIG_ID = ? ";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, stationParamterId);
            statement.execute();

            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
            return defaultResponseDTO;
        }catch (Exception e) {
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Xóa thất bại: "+ e.getMessage());
            return defaultResponseDTO;
        }
    }


    @PostMapping("/get-list-series-time-config-pagination")
    public DefaultPaginationDTO getListSeriesTimeConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {

        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*,b.TS_CONFIG_ID,b.uuid from TIME_SERIES_TYPE a, TIME_SERIES_CONFIG b where a.TS_TYPE_ID = b.TS_TYPE_ID ");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    Map<String,String> params = objectMapper.readValue(search, Map.class);
                    if (Strings.isNotEmpty(params.get("s_uuid"))) {
                        sql.append(" and b.uuid = ?");
                        paramSearch.add(params.get("s_uuid"));
//                        paramSearch.add(params.get("s_stationId"));
                    }
                    if (params.get("s_stationId") != null) {
                        sql.append(" and b.PARAMETER_TYPE_ID = ? ");
                        paramSearch.add(params.get("s_stationId"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.debug("SQL get-list-series-time-config-pagination : {}", sql.toString());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<Parameter> list = new ArrayList<>();

            while (rs.next()) {
                Parameter bo = Parameter.builder()
                        .stationParamterId(rs.getInt("TS_CONFIG_ID"))
                        .paramterTypeId(rs.getInt("TS_TYPE_ID"))
                        .parameterName(rs.getString("TS_TYPE_NAME"))
                        .unitName(rs.getString("TS_TYPE_DESCRIPTION"))
                        .uuid(rs.getString("UUID"))
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

    @PostMapping("/get-list-station-parameter-pagination")
    public DefaultPaginationDTO getListStationParameterPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.*,b.PARAMETER_TYPE_NAME, c.UNIT_NAME from PARAMETER a, PARAMETER_TYPE b, unit c where a.PARAMETER_TYPE_ID = b.PARAMETER_TYPE_ID(+) and b.UNIT_ID = c.UNIT_ID(+) ");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                	Map<String,String> params = objectMapper.readValue(search, Map.class);
                	if (params.get("s_stationId") == null && Strings.isNotEmpty(params.get("s_uuid"))) {
                        sql.append(" and (uuid = ?)");
                        paramSearch.add(params.get("s_uuid"));
//                        paramSearch.add(params.get("s_stationId"));
                    }
                	if (params.get("s_stationId") != null) {
                        sql.append(" and STATION_ID = ? ");
                        paramSearch.add(params.get("s_stationId"));
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
    	DefaultResponseDTO defaultResponseDTO = stationManagementService.saveOrUpdateStationTimeSeriesPLSQL(params,true);
    	return defaultResponseDTO;
    }
    
    @PostMapping("/update-station-time-series")
    public DefaultResponseDTO updateStationTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
    	DefaultResponseDTO defaultResponseDTO = stationManagementService.saveOrUpdateStationTimeSeriesPLSQL(params,false);
    	return defaultResponseDTO;
    }
    
    @PostMapping("/delete-station-time-series")
    public DefaultResponseDTO deleteStationTimeSeries(@RequestParam(name="stationId") @Valid String stationId) throws SQLException, JsonProcessingException {
    	DefaultResponseDTO defaultResponseDTO = stationManagementService.deleteStationTimeSeriesPLSQL(stationId);
    	return defaultResponseDTO;
    }

    @PostMapping("/create-time-series-config-parameter")
    public DefaultResponseDTO createTimeSeriesConfigParameter(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = stationManagementService.saveOrUpdateTimeSeriesConfigParameterPLSQL(params,true);
        return defaultResponseDTO;
    }

    @PostMapping("/update-time-series-config-parameter")
    public DefaultResponseDTO updateTimeSeriesConfigParameter(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = stationManagementService.saveOrUpdateTimeSeriesConfigParameterPLSQL(params,false);
        return defaultResponseDTO;
    }

    @PostMapping("/delete-time-series-config-parameter")
    public DefaultResponseDTO deleteTimeSeriesConfigParameter(@RequestParam(name="parameterId") @Valid String parameterId) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = stationManagementService.deleteTimeSeriesConfigParameterPLSQL(parameterId);
        return defaultResponseDTO;
    }

    @PostMapping("/control")
    public DefaultResponseDTO controlStation(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String result = "";
        try(Socket socketOfClient = new Socket(params.get("host"), Integer.parseInt(params.get("port")));
            BufferedReader is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));) {

            os.write(params.get("command"));
            os.newLine(); // kết thúc dòng
            os.flush();  // đẩy dữ liệu đi.

            String responseLine;
            while ((responseLine = is.readLine()) != null) {
                result += responseLine + "\n";
                if (responseLine.indexOf("OK") != -1) {
                    break;
                }
            }
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage(result);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + params.get("host"));
            result = e.getMessage();
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage(result);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + params.get("host"));
            result = e.getMessage();
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage(result);
        }
        return defaultResponseDTO;
    }

    @GetMapping("/get-select-time-series-config")
    public List<ComboBox> getListTimeSeriesConfigCombobox(@RequestParam(name = "parameterId") String parameterId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select * from time_series_config WHERE 1 = 1 ");
        if(parameterId != null){
            sql.append(" and PARAMETER_TYPE_ID = ? ");
        }
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            st.setString(1,parameterId);
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("TS_CONFIG_ID"))
                        .text(rs.getString("TS_CONFIG_ID") + " - " + rs.getString("TS_CONFIG_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }

    @PostMapping("/create-time-series")
    public DefaultResponseDTO createTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException, JsonProcessingException {
        log.info(objectMapper.writeValueAsString(params));
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "insert into TIME_SERIES(TS_ID, TS_NAME, UUID, TS_CONFIG_ID,STATION_ID) values (TIME_SERIES_SEQ.nextval,?,?,?,?)";
        try (Connection connection = ds.getConnection();PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
            statement.setString(i++, params.get("tsName"));
            statement.setString(i++, params.get("uuid"));
            statement.setString(i++, params.get("tsConfigId"));
            statement.setString(i++, params.get("stationId"));
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

    @PostMapping("/get-list-time-series-pagination")
    public DefaultPaginationDTO getListTimeSeriesPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {

        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.* from TIME_SERIES a, TIME_SERIES_CONFIG b where a.TS_CONFIG_ID = b.TS_CONFIG_ID(+)");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    Map<String,String> params = objectMapper.readValue(search, Map.class);
                    if (params.get("s_stationId") == null && Strings.isNotEmpty(params.get("s_uuid"))) {
                        sql.append(" and (a.uuid = ?)");
                        paramSearch.add(params.get("s_uuid"));
//                        paramSearch.add(params.get("s_stationId"));
                    }
                    if (params.get("s_stationId") != null) {
                        sql.append(" and a.STATION_ID = ? ");
                        paramSearch.add(params.get("s_stationId"));
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
                        .stationParamterId(rs.getInt("TS_ID"))
                        .paramterTypeId(rs.getInt("TS_CONFIG_ID"))
                        .parameterName(rs.getString("TS_NAME"))
                        .stationId(rs.getInt("STATION_ID"))
//                        .timeFrequency(rs.getInt("TIME_FREQUENCY"))
                        .uuid(rs.getString("UUID"))
//                        .unitName(rs.getString("UNIT_NAME"))
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
}
