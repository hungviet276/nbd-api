package com.neo.nbdapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.utils.ExcelUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportExcelService {
    @Autowired
    private HikariDataSource ds;

    @Autowired
    private StationManagementService stationManagementService;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    public InputStreamResource createExel(String stringSearch, String fileName) {
        fileName = fileName.trim();
        List<Map> fileContents = null;
        String FILE_EXT = ".xlsx";
        if (fileName.equals("history")) fileContents = createContentFileHistory(stringSearch);
        else if (fileName.equals("adcp_vi")) fileContents = createContentFileAdcp(stringSearch);
        else if (fileName.equals("liss_vi")) fileContents = createContentFileLiss(stringSearch);
        String fileIn = "templates/" + fileName + FILE_EXT;
        return new InputStreamResource(ExcelUtils.write2File(fileContents, fileIn, 0, 3));
    }

    private List<Map> createContentFileHistory(String stringSearch) {
        List<Map> fileContents = null;
        try (Connection connection = ds.getConnection()) {
            StringBuilder sql = new StringBuilder("select a.*, b1.AREA_NAME, b.PROVINCE_NAME, c.DISTRICT_NAME,");
            sql.append(" e.RIVER_NAME, d.WARD_NAME, g.* from stations_his a , AREAS b1, PROVINCES b,");
            sql.append(" DISTRICTS c, WARDS d, rivers e , stations_object_type f , OBJECT_TYPE g \n");
            sql.append("  where a.area_id = b1.area_id(+) and a.PROVINCE_ID = b.PROVINCE_ID(+) ");
            sql.append("and a.DISTRICT_ID = c.DISTRICT_ID(+) and a.WARD_ID = d.WARD_ID(+) ");
            sql.append("and a.RIVER_ID = e.RIVER_ID(+) and a.STATION_ID = f.STATION_ID ");
            sql.append("and f.OBJECT_TYPE_ID = g.OBJECT_TYPE_ID");

            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(stringSearch)) {
                Map<String, String> params = objectMapper.readValue(stringSearch, Map.class);
                if (Strings.isNotEmpty(params.get("stationTypeId")) && !"-1".equals(params.get("stationTypeId"))) {
                    sql.append(" and g.OBJECT_TYPE_ID = ? ");
                    paramSearch.add(params.get("stationTypeId"));
                }
                if (Strings.isNotEmpty(params.get("inputFromDate"))) {
                    sql.append(" and a.CREATED_AT > to_date(?,'dd/mm/yyyy') ");
                    paramSearch.add(params.get("inputFromDate"));
                }
                if (Strings.isNotEmpty(params.get("inputToDate"))) {
                    sql.append(" and a.CREATED_AT < to_date(?,'dd/mm/yyyy') + 1 ");
                    paramSearch.add(params.get("inputToDate"));
                }
                if (Strings.isNotEmpty(params.get("s_objectType"))) {
                    sql.append(" and g.OBJECT_TYPE = ? ");
                    paramSearch.add(params.get("s_objectType"));
                }
                if (Strings.isNotEmpty(params.get("s_stationCode"))) {
                    sql.append(" and a.station_code = ? ");
                    paramSearch.add(params.get("s_stationCode"));
                }
                if (Strings.isNotEmpty(params.get("s_stationName"))) {
                    sql.append(" and lower(a.STATION_NAME) like lower(?) ");
                    paramSearch.add("%" + params.get("s_stationName") + "%");
                }
                if (Strings.isNotEmpty(params.get("s_createById"))) {
                    sql.append(" and lower(a.CREATED_BY_ID) = lower(?) ");
                    paramSearch.add(params.get("s_createById"));
                }
                sql.append(" order by a.CREATED_AT desc ");

                long maxRows = paginationDAO.countResultQuery(sql.toString(), paramSearch);
                ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), 1, (int) maxRows, paramSearch);
                fileContents = stationManagementService.getRsListMapStationPagination(rs);
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContents;
    }

    private List<Map> createContentFileAdcp(String stringSearch) {
        List<Map> result = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            StringBuilder sql = new StringBuilder("select a.*,liss.TOTAL_TURB,liss.SUSPENDED_MATERIAL, ");
            sql.append("b.station_code, b.STATION_NAME, r.RIVER_ID, r.RIVER_NAME, d.OBJECT_TYPE, ");
            sql.append("d.OBJECT_TYPE_SHORTNAME from adcp a, liss , stations b, stations_object_type c, ");
            sql.append("OBJECT_TYPE d , RIVERS r \n");
            sql.append("where a.STATION_ID = liss.STATION_ID(+) and a.WATER_FLOW = liss.WATER_FLOW(+) ");
            sql.append("and a.STATION_ID = b.STATION_ID and b.river_id = r.river_id and a.STATION_ID = ");
            sql.append("c.STATION_ID and c.OBJECT_TYPE_ID = d.OBJECT_TYPE_ID ");

            if (!Strings.isNotEmpty(stringSearch)) return result;

            Map<String, String> params = objectMapper.readValue(stringSearch, Map.class);
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(params.get("s_objectType"))) {
                sql.append(" and d.OBJECT_TYPE = ? ");
                paramSearch.add(params.get("s_objectType"));
            }
            if (Strings.isNotEmpty(params.get("s_objectTypeName"))) {
                sql.append(" and lower(d.OBJECT_TYPE_SHORTNAME) like lower(?) ");
                paramSearch.add("%" + params.get("s_objectTypeName") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_stationCode"))) {
                sql.append(" and b.station_code = ? ");
                paramSearch.add(params.get("s_stationCode"));
            }
            if (Strings.isNotEmpty(params.get("s_stationName"))) {
                sql.append(" and lower(b.STATION_NAME) like lower(?) ");
                paramSearch.add("%" + params.get("s_stationName") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_riverName"))) {
                sql.append(" and r.river_name like ? ");
                paramSearch.add("%" + params.get("s_riverName") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_timeStart"))) {
                sql.append(" and a.TIME_START >= to_date(?,'DD/MM/YYYY HH24:MI') ");
                paramSearch.add(params.get("s_timeStart"));
            }
            if (Strings.isNotEmpty(params.get("s_timeEnd"))) {
                sql.append(" and a.time_end <= to_date(?,'DD/MM/YYYY HH24:MI') ");
                paramSearch.add(params.get("s_timeEnd"));
            }
            long maxRows = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            sql.append(" order by a.CREATED_AT desc ");
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), 1, (int) maxRows, paramSearch);

            while (rs.next()) {
                Map bo = new HashMap();
                bo.put("id", rs.getLong("ID"));
                bo.put("objectType", rs.getString("STATION_ID"));
                bo.put("objectName", rs.getString("OBJECT_TYPE_SHORTNAME"));
                bo.put("stationCode", rs.getString("station_code"));
                bo.put("stationName", rs.getString("STATION_NAME"));
                bo.put("riverName", rs.getString("RIVER_NAME"));
                bo.put("timeStart", rs.getDate("TIME_START"));
                bo.put("timeEnd", rs.getDate("TIME_END"));
                bo.put("timeAvg", rs.getDate("TIME_AVG"));
                bo.put("waterFlow", rs.getFloat("WATER_FLOW"));
                bo.put("totalTurb", rs.getFloat("TOTAL_TURB"));
                bo.put("suspendedMaterial", rs.getFloat("SUSPENDED_MATERIAL"));
                //
                bo.put("widthRiver", rs.getFloat("WIDTH_RIVER"));
                bo.put("speedAvg", rs.getFloat("SPEED_AVG"));
                bo.put("speedMax", rs.getFloat("SPEED_MAX"));
                bo.put("deepAvg", rs.getFloat("DEEP_AVG"));
                bo.put("deepMax", rs.getFloat("DEEP_MAX"));
                bo.put("waterLevelStart", rs.getFloat("WATER_LEVEL_START"));
                bo.put("waterLevelEnd", rs.getFloat("WATER_LEVEL_END"));
                bo.put("waterLevelAvg", rs.getFloat("WATER_LEVEL_AVG"));
                bo.put("note", rs.getString("NOTE"));
                bo.put("createAt", rs.getDate("CREATED_AT"));
                bo.put("squareRiver", rs.getFloat("SQUARE_RIVER"));
                result.add(bo);
            }

        } catch (SQLException | BusinessException throwables) {
            throwables.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Map> createContentFileLiss(String stringSearch) {
        StringBuilder sql = new StringBuilder("select a.* ,b.station_code, b.STATION_NAME, r.RIVER_ID, \n");
        sql.append("r.RIVER_NAME, d.OBJECT_TYPE, d.OBJECT_TYPE_SHORTNAME from liss a, stations b, \n");
        sql.append("stations_object_type c, OBJECT_TYPE d , RIVERS r\n");
        sql.append("    where a.STATION_ID = b.STATION_ID and b.river_id = r.river_id \n");
        sql.append("    and a.STATION_ID = c.STATION_ID and c.OBJECT_TYPE_ID = d.OBJECT_TYPE_ID \n");

        List<Map> fileContents = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            if (!Strings.isNotEmpty(stringSearch)) return fileContents;

            Map<String, String> params = objectMapper.readValue(stringSearch, Map.class);
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(params.get("s_objectTypeLISS"))) {
                sql.append(" and d.OBJECT_TYPE = ? ");
                paramSearch.add(params.get("s_objectTypeLISS"));
            }
            if (Strings.isNotEmpty(params.get("s_objectTypeNameLISS"))) {
                sql.append(" and lower(d.OBJECT_TYPE_SHORTNAME) like lower(?) ");
                paramSearch.add("%" + params.get("s_objectTypeNameLISS") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_stationCodeLISS"))) {
                sql.append(" and b.station_code = ? ");
                paramSearch.add(params.get("s_stationCodeLISS"));
            }
            if (Strings.isNotEmpty(params.get("s_stationNameLISS"))) {
                sql.append(" and lower(b.STATION_NAME) like lower(?) ");
                paramSearch.add("%" + params.get("s_stationNameLISS") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_riverNameLISS"))) {
                sql.append(" and r.river_name like ? ");
                paramSearch.add("%" + params.get("s_riverNameLISS") + "%");
            }
            if (Strings.isNotEmpty(params.get("s_timeStartLISS"))) {
                sql.append(" and a.TIME_START >= to_date(?,'DD/MM/YYYY HH24:MI') ");
                paramSearch.add(params.get("s_timeStartLISS"));
            }
            if (Strings.isNotEmpty(params.get("s_timeEndLISS"))) {
                sql.append(" and a.time_end <= to_date(?,'DD/MM/YYYY HH24:MI') ");
                paramSearch.add(params.get("s_timeEndLISS"));
            }
            long maxRows = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            sql.append(" order by a.CREATED_AT desc ");
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), 1, (int) maxRows, paramSearch);

            while (rs.next()) {
                Map bo = new HashMap();
                bo.put("id", rs.getLong("ID"));
                bo.put("objectType", rs.getString("STATION_ID"));
                bo.put("objectName", rs.getString("OBJECT_TYPE_SHORTNAME"));
                bo.put("stationCode", rs.getString("station_code"));
                bo.put("stationName", rs.getString("STATION_NAME"));
                bo.put("riverName", rs.getString("RIVER_NAME"));
                bo.put("timeStart", rs.getDate("TIME_START"));
                bo.put("timeEnd", rs.getDate("TIME_END"));
                bo.put("timeAvg", rs.getDate("TIME_AVG"));
                bo.put("waterFlow", rs.getFloat("WATER_FLOW"));
                bo.put("totalTurb", rs.getFloat("TOTAL_TURB"));
                bo.put("suspendedMaterial", rs.getFloat("SUSPENDED_MATERIAL"));

                fileContents.add(bo);
            }

        } catch (SQLException | BusinessException throwables) {
            throwables.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return fileContents;
    }

}
