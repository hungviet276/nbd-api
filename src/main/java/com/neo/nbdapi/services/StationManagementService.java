package com.neo.nbdapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

@Component
public class StationManagementService {
    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;
    
    @Autowired
    CommonService commonService;

    DefaultPaginationDTO getListMailConfigPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        DefaultPaginationDTO result = null;
        return result;
    }

    DefaultResponseDTO createMailConfig(CreateMailConfigVM createMailConfigVM) throws SQLException{
        DefaultResponseDTO result = null;
        return result;
    }
    
    public DefaultResponseDTO createStationTimeSeries(@RequestBody @Valid Map<String,String> params) throws SQLException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        
    	Connection con = null;
    	try {
    		con = ds.getConnection();
    		con.setAutoCommit(false);
    		//save station
    		defaultResponseDTO = saveStation(con, params);
    		if(Objects.equals(defaultResponseDTO.getMessage(), "OK")) {
    			//save station type
    			params.put("stationId", Integer.toString(defaultResponseDTO.getStatus()));
    			defaultResponseDTO = saveStationTypeObject(con, params);
    			defaultResponseDTO = saveStationSeriesTime(con, params);
    		}else {
    			defaultResponseDTO.setStatus(0);
                defaultResponseDTO.setMessage("Lỗi khi thêm mới");
    		}
    	}catch (Exception e) {
    		con.rollback();
			if(con != null) {
				con.close();
			}
		}
    	defaultResponseDTO.setStatus(1);
        defaultResponseDTO.setMessage("Thêm mới thành công");
        return defaultResponseDTO;
    }
    
    private DefaultResponseDTO saveStation(Connection connection,Map<String,String> params) throws SQLException, BusinessException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "INSERT INTO STATIONS(STATION_ID, STATION_CODE, STATION_NAME, LONGITUDE, LATITUDE, ADDRESS, STATUS, "
    			+ " DISTRICT_ID, PROVINCE_ID, RIVER_ID, STATION_TYPE_ID, WARD_ID,IS_ACTIVE,CREATED_AT) "
        		+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
    	Long seq = commonService.getSequence("STATIONS_SEQ");
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            int idx = 1;
            statement.setLong(idx++, seq);
            statement.setString(idx++, params.get("stationCode"));
            statement.setString(idx++, params.get("stationName"));
            statement.setString(idx++, params.get("longStation"));
            statement.setString(idx++, params.get("latStation"));
            statement.setString(idx++, params.get("address"));
            statement.setString(idx++, params.get("status"));
            statement.setString(idx++, params.get("districtId"));
            statement.setString(idx++, params.get("provinceId"));
            statement.setString(idx++, params.get("riverId"));
            statement.setString(idx++, params.get("stationTypeId"));
            statement.setString(idx++, params.get("wardId"));
            statement.setInt(idx++, 1);
            
            statement.execute();
            
            defaultResponseDTO.setStatus(seq.intValue());
            defaultResponseDTO.setMessage("OK");
            
        }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Lỗi thực hiện :"+ e.getMessage());
		}
        return defaultResponseDTO;
    }
    
    private DefaultResponseDTO saveStationTypeObject(Connection connection,Map<String,String> params) throws SQLException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "INSERT INTO STATIONS_OBJECT_TYPE(STATIONS_OBJECT_TYPE_ID, STATIONS_ID, OBJECT_TYPE_ID) "
        		+ "values (STATIONS_OBJECT_TYPE_SEQ.nextval, ?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            int idx = 1;
            statement.setString(idx++, params.get("stationId"));
            statement.setString(idx++, params.get("stationTypeAddId"));
            
            statement.execute();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("OK");
        }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Lỗi thực hiện :"+ e.getMessage());
		}
        return defaultResponseDTO;
    }
    
    private DefaultResponseDTO saveParamter(Connection connection,Map<String,String> params) throws SQLException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "INSERT INTO PARAMETER_KTTV(STATION_PARAMETER_ID, PARAMETER_TYPE_ID, STATION_ID, TIME_FREQUENCY) "
        		+ "values (PARAMETER_KTTV_SEQ.nextval, ?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            int idx = 1;
            statement.setString(idx++, params.get("parameter"));
            statement.setString(idx++, params.get("stationId"));
            statement.setString(idx++, params.get("frequency"));
            
            statement.execute();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("OK");
        }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Lỗi thực hiện :"+ e.getMessage());
		}
        return defaultResponseDTO;
    }
    
    private DefaultResponseDTO saveStationSeriesTime(Connection connection,Map<String,String> params) throws SQLException {
    	DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
    	String sql = "INSERT INTO STATION_TIME_SERIES(ID,TS_ID, TS_NAME, STATION_ID, TS_TYPE_ID, PARAMETERTYPE_ID, PARAMETERTYPE_NAME, "
	    		+ "PARAMETERTYPE_DESCRIPTION, STATION_NO, STATION_NAME, STATION_LONGNAME, STATION_LATITUDE, STATION_LONGTITUDE, CATCHMENT_ID, "
	    		+ "CATCHMENT_NAME, SITE_ID, SITE_NAME, RIVER_ID, RIVER_NAME, PROVINCE_ID, PROVINCE_NAME, DISTRICT_ID, DISTRICT_NAME, WARD_ID, "
	    		+ "WARD_NAME, COUNTRY_ID, COUNTRY_NAME, PROJECT_ID, PROJECT_NAME, STORAGE, CREATE_DATE) "
	    		+ "values (STATION_TIME_SERIES_SEQ.nextval, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
	    try (PreparedStatement statement = connection.prepareStatement(sql);) {
	        int idx = 1;
	        statement.setString(idx++, params.get("tsId"));
	        statement.setString(idx++, params.get("tsName"));
	        statement.setString(idx++, params.get("stationId"));
	        statement.setString(idx++, params.get("tsTypeId"));
	        statement.setString(idx++, params.get("parameterTypeId"));
	        statement.setString(idx++, params.get("paramterTypeName"));
	        statement.setString(idx++, params.get("parameterTypeDes"));
	        statement.setString(idx++, params.get("stationCode"));
	        statement.setString(idx++, params.get("stationName"));
	        statement.setString(idx++, params.get("stationLongName"));
	        statement.setString(idx++, params.get("longtitude"));
	        statement.setString(idx++, params.get("lattitude"));
	        statement.setString(idx++, params.get("catchementId"));
	        statement.setString(idx++, params.get("catchementName"));
	        statement.setString(idx++, params.get("siteId"));
	        statement.setString(idx++, params.get("siteName"));
	        statement.setString(idx++, params.get("riverId"));
	        statement.setString(idx++, params.get("riverName"));
	        statement.setString(idx++, params.get("provinceId"));
	        statement.setString(idx++, params.get("provinceName"));
	        statement.setString(idx++, params.get("districtId"));
	        statement.setString(idx++, params.get("districtName"));
	        statement.setString(idx++, params.get("wardId"));
	        statement.setString(idx++, params.get("wardName"));
	        statement.setString(idx++, params.get("countryId"));
	        statement.setString(idx++, params.get("countryName"));
	        statement.setString(idx++, params.get("projectId"));
	        statement.setString(idx++, params.get("projectName"));
	        statement.setString(idx++, params.get("storage"));
	        statement.execute();
	        defaultResponseDTO.setStatus(1);
	        defaultResponseDTO.setMessage("Thêm mới thành công");
	        return defaultResponseDTO;
	    }catch (Exception e) {
        	defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Lỗi thực hiện :"+ e.getMessage());
		}
        return defaultResponseDTO;
    }
}
