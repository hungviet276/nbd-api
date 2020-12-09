/**
 * 
 */
package com.neo.nbdapi.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neo.nbdapi.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/common")
public class CommonController {
	
	private Logger logger = LogManager.getLogger(CommonController.class);
	
	@Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;
    
    @GetMapping("/get-list-rivers")
    public List<River> getListRivers() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from RIVERS WHERE 1 = 1 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<River> list = new ArrayList<>();
            while (rs.next()) {
            	River bo = River.builder()
                        .riverId(rs.getInt("RIVER_ID"))
                        .riverCode(rs.getString("RIVER_CODE"))
                        .status(rs.getInt("RIVER_CODE"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-list-wards")
    public List<Ward> getListWard(@RequestParam(name = "provinceId") String provinceId,@RequestParam(name = "districtId") String districtId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from WARDS WHERE 1 = 1 ");
    	if(!Strings.isEmpty(provinceId)) {
    		sql.append(" and PROVINCE_ID = ? ");
    	}
    	if(!Strings.isEmpty(districtId)) {
    		sql.append(" and DISTRICT_ID = ? ");
    	}
    	try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
    		st.setString(1, provinceId);
    		st.setString(2, districtId);
            ResultSet rs = st.executeQuery();
            List<Ward> list = new ArrayList<>();
            while (rs.next()) {
            	Ward bo = Ward.builder()
                        .wardId(rs.getInt("WARD_ID"))
                        .wardCode(rs.getString("WARD_CODE"))
                        .status(rs.getInt("WARD_CODE"))
                        .wardLong(rs.getFloat("LONG"))
                        .wardLat(rs.getFloat("LAT"))
                        .districtId(rs.getInt("DISTRICT_ID"))
                        .provinceId(rs.getInt("PROVINCE_ID"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-list-provinces")
    public List<Province> getListProvinces(@RequestParam(name = "areaId") String areaId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from PROVINCES WHERE 1 = 1 ");
    	if(!Strings.isEmpty(areaId)) {
    		sql.append(" and AREA_ID = ? ");
    	}
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(areaId)) {
        		st.setString(1, areaId);
        	}
            ResultSet rs = st.executeQuery();
            List<Province> list = new ArrayList<>();
            while (rs.next()) {
            	Province bo = Province.builder()
                        .provinceId(rs.getInt("PROVINCE_ID"))
                        .provinceCode(rs.getString("PROVINCE_CODE"))
                        .provinceName(rs.getString("PROVINCE_NAME"))
                        .provinceLong(rs.getFloat("LONG"))
                        .provinceLat(rs.getFloat("LAT"))
                        .status(rs.getInt("STATUS"))
                        .areaId(rs.getInt("AREA_ID"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-list-district")
    public List<District> getListDistrict(@RequestParam(name = "provinceId") String provinceId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from DISTRICTS WHERE 1 = 1 ");
    	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
    		sql.append(" and PROVINCE_ID = ? ");
    	}
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
        		st.setString(1, provinceId);
        	}
            ResultSet rs = st.executeQuery();
            List<District> list = new ArrayList<>();
            while (rs.next()) {
            	District bo = District.builder()
                        .districtId(rs.getInt("DISTRICT_ID"))
                        .districtCode(rs.getString("DISTRICT_CODE"))
                        .districtName(rs.getString("DISTRICT_NAME"))
                        .districtLong(rs.getFloat("LONG"))
                        .districtLat(rs.getFloat("LAT"))
                        .status(rs.getInt("STATUS"))
                        .areaId(rs.getInt("AREA_ID"))
                        .provinceId(rs.getInt("PROVINCE_ID"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-rivers")
    public List<ComboBox> getListRiversCombobox() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from RIVERS WHERE 1 = 1 order by RIVER_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("RIVER_ID"))
                        .text(rs.getString("RIVER_CODE") + " - " + rs.getString("RIVER_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-provinces")
    public List<ComboBox> getListProvincesCombobox(@RequestParam(name = "areaId") String areaId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from PROVINCES WHERE 1 = 1 ");
    	if(!Strings.isEmpty(areaId) && !"null".equals(areaId)) {
    		sql.append(" and AREA_ID = ? ");
    	}
    	sql.append(" order by PROVINCE_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(areaId) && !"null".equals(areaId)) {
        		st.setString(1, areaId);
        	}
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("PROVINCE_ID"))
                        .text(rs.getString("PROVINCE_CODE") + " - " + rs.getString("PROVINCE_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-district")
    public List<ComboBox> getListDistrictCombobox(@RequestParam(name = "provinceId") String provinceId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from DISTRICTS WHERE 1 = 1 ");
    	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
    		sql.append(" and PROVINCE_ID = ? ");
    	}
    	sql.append(" order by DISTRICT_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
        		st.setString(1, provinceId);
        	}
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("DISTRICT_ID"))
                        .text(rs.getString("DISTRICT_CODE") + " - " + rs.getString("DISTRICT_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-ward")
    public List<ComboBox> getListWardCombobox(@RequestParam(name = "provinceId") String provinceId,@RequestParam(name = "districtId") String districtId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from WARDS WHERE 1 = 1");
    	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
    		sql.append(" and PROVINCE_ID = ? ");
    	}
    	if(!Strings.isEmpty(districtId) && !"null".equals(districtId)) {
    		sql.append(" and DISTRICT_ID = ? ");
    	}
    	sql.append(" order by WARD_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(provinceId) && !"null".equals(provinceId)) {
        		st.setString(1, provinceId);
        	}
        	if(!Strings.isEmpty(districtId) && !"null".equals(districtId)) {
        		st.setString(2, districtId);
        	}
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("WARD_ID"))
                        .text(rs.getString("WARD_CODE") + " - " + rs.getString("WARD_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }
    
    @GetMapping("/get-list-unit")
    public List<Unit> getListUnit() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from unit WHERE 1 = 1 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            ResultSet rs = st.executeQuery();
            List<Unit> list = new ArrayList<>();
            while (rs.next()) {
            	Unit bo = Unit.builder()
                        .unitId(rs.getInt("UNIT_ID"))
                        .unitCode(rs.getString("UNIT_CODE"))
                        .unitName(rs.getString("UNIT_NAME"))
                        .status(rs.getInt("STATUS"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-unit")
    public List<ComboBox> getListUnitCombobox() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from unit WHERE 1 = 1 order by UNIT_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("UNIT_ID"))
                        .text(rs.getString("UNIT_CODE") + " - " + rs.getString("UNIT_NAME"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }

    @GetMapping("/get-select-list-timeseries")
    public List<ComboBox> getListTimeseriesCombobox(@RequestParam Map<String,String> params) throws SQLException, BusinessException {
        String sql = "";
        StringBuilder stringBuilder = new StringBuilder("select * from TIME_SERIES_TYPE WHERE 1 = 1 ");
        if(params != null && params.size() > 0){
            stringBuilder.append(" and TS_TYPE_ID not in(%s,-1)");
        }
        sql = stringBuilder.toString();
        if(params != null && params.size() > 0) {
            sql = String.format(sql, (params.get("tsTypeId") != null && !"".equals(params.get("tsTypeId"))) ? params.get("tsTypeId") : "-1");
        }
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql); ) {
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox bo = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(bo);
            while (rs.next()) {
                bo = ComboBox.builder()
                        .id(rs.getLong("TS_TYPE_ID"))
                        .text(rs.getString("TS_TYPE_NAME"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }

    @GetMapping("/get-list-parameter")
    public List<Unit> getListParameterType() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from PARAMETER_TYPE WHERE 1 = 1 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            ResultSet rs = st.executeQuery();
            List<Unit> list = new ArrayList<>();
            while (rs.next()) {
            	Unit bo = Unit.builder()
                        .unitId(rs.getInt("UNIT_ID"))
                        .unitCode(rs.getString("UNIT_CODE"))
                        .unitName(rs.getString("UNIT_NAME"))
                        .status(rs.getInt("STATUS"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-parameter") 
    public List<ComboBox> getListParameterCombobox() throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from PARAMETER_TYPE WHERE 1 = 1  order by PARAMETER_TYPE_NAME");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox stationType = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBox.builder()
                        .id(rs.getLong("PARAMETER_TYPE_ID"))
                        .text(rs.getString("PARAMETER_TYPE_NAME") + " - " + rs.getString("PARAMETER_TYPE_DESCRIPTION"))
                        .build();
                list.add(stationType);
            }
            return list;
        }
    }
    
    @GetMapping("/get-list-areas")
    public List<Area> getListAreas(@RequestParam(name = "countryId") String countryId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from AREAS WHERE 1 = 1 ");
    	if(!Strings.isEmpty(countryId)) {
    		sql.append(" and COUNTRY_ID = ? ");
    	}
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(countryId)) {
        		st.setString(1, countryId);
        	}
            ResultSet rs = st.executeQuery();
            List<Area> list = new ArrayList<>();
            while (rs.next()) {
            	Area bo = Area.builder()
                        .areaId(rs.getInt("AREA_ID"))
                        .areaCode(rs.getString("AREA_CODE"))
                        .areaName(rs.getString("AREA_NAME"))
//                        .createById(rs.getInt("CREATED_BY_ID"))
//                        .updatedById(rs.getInt("UPDATED_BY_ID"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }
    
    @GetMapping("/get-select-list-areas")
    public List<ComboBox> getListAreasCombobox(@RequestParam(name = "countryId") String countryId) throws SQLException, BusinessException {
    	StringBuilder sql = new StringBuilder("select * from AREAS WHERE 1 = 1 ");
    	if(!Strings.isEmpty(countryId)) {
    		sql.append(" and COUNTRY_ID = ? ");
    	}
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
        	if(!Strings.isEmpty(countryId)) {
        		st.setString(1, countryId);
        	}
            ResultSet rs = st.executeQuery();
            List<ComboBox> list = new ArrayList<>();
            ComboBox bo = ComboBox.builder().id(-1L).text("Lựa chọn").build();
            list.add(bo);
            while (rs.next()) {
                bo = ComboBox.builder()
                        .id(rs.getLong("AREA_ID"))
                        .text(rs.getString("AREA_CODE") + " - " + rs.getString("AREA_NAME"))
                        .build();
                list.add(bo);
            }
            return list;
        }
    }

    @GetMapping("/get-select-list-staff")
    public List<ComboBoxStr> getListStaffCombobox() throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select * from user_info WHERE STATUS_ID = 1 ");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBoxStr> list = new ArrayList<>();
            ComboBoxStr comboBox = ComboBoxStr.builder().id("-1").text("Lựa chọn").build();
            list.add(comboBox);
            while (rs.next()) {
                comboBox = ComboBoxStr.builder()
                        .id(rs.getString("id"))
                        .text(rs.getString("ID") + " - " + rs.getString("NAME"))
                        .build();
                list.add(comboBox);
            }
            return list;
        }
    }

    @GetMapping("/get-select-list-nth-flow")
    public List<ComboBoxStr> getListNthFlowCombobox(@RequestParam(name = "stationId") String stationId) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select MEASURE_NTH,WATER_FLOW from adcp where STATION_ID = ?  order by MEASURE_NTH");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            st.setString(1,stationId);
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBoxStr> list = new ArrayList<>();
            ComboBoxStr comboBox = ComboBoxStr.builder().id("-1").text("Lựa chọn").build();
            list.add(comboBox);
            while (rs.next()) {
                comboBox = ComboBoxStr.builder()
                        .id(rs.getString("WATER_FLOW"))
                        .text(rs.getString("MEASURE_NTH"))
                        .build();
                list.add(comboBox);
            }
            return list;
        }
    }

    @GetMapping("/get-station-ip")
    public List<DataLogger> getStationsIP(@RequestParam(name = "stationCode") String stationCode) throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder("select * from DATA_LOGGERS where DATA_LOGGER_CODE = ?");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString()); ) {
            st.setString(1,stationCode);
            ResultSet rs = st.executeQuery();
            List<DataLogger> list = new ArrayList<>();
            while (rs.next()) {
                DataLogger comboBox = DataLogger.builder()
                        .dataLoggerId(rs.getLong("DATA_LOGGER_ID"))
                        .dataLoggerCode(rs.getString("DATA_LOGGER_CODE"))
                        .modem(rs.getString("MODEM"))
                        .port(rs.getInt("PORT"))
                        .build();
                list.add(comboBox);
            }
            return list;
        }
    }
}
