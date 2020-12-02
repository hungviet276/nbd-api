package com.neo.nbdapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ADCP;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.StationManagementService;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/manual-input")
public class LISSController {
    private Logger logger = LogManager.getLogger(StationTypeController.class);

    private Logger loggerAction = LogManager.getLogger("ActionCrud");

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private StationManagementService stationManagementService;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @PostMapping("/get-list-liss-pagination")
    public DefaultPaginationDTO getListLISSPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {

        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("select a.* ,b.station_code, b.STATION_NAME, r.RIVER_ID, r.RIVER_NAME, d.OBJECT_TYPE, d.OBJECT_TYPE_SHORTNAME from liss a, stations b, stations_object_type c, OBJECT_TYPE d , RIVERS r\n" +
                    "    where a.STATION_ID = b.STATION_ID and b.river_id = r.river_id and a.STATION_ID = c.STATION_ID and c.OBJECT_TYPE_ID = d.OBJECT_TYPE_ID ");
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    Map<String, String> params = objectMapper.readValue(search, Map.class);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sql.append(" order by a.CREATED_AT desc ");
            log.info(sql.toString());
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            List<ADCP> list = new ArrayList<>();

            while (rs.next()) {
                ADCP bo = ADCP.builder()
                        .id(rs.getLong("ID"))
                        .stationId(rs.getString("STATION_ID"))
                        .objectType(rs.getString("OBJECT_TYPE"))
                        .objectName(rs.getString("OBJECT_TYPE_SHORTNAME"))
                        .stationCode(rs.getString("station_code"))
                        .stationName(rs.getString("STATION_NAME"))
                        .riverName(rs.getString("RIVER_NAME"))
                        .timeStart(rs.getDate("TIME_START"))
                        .timeEnd(rs.getDate("TIME_END"))
                        .timeAvg(rs.getDate("TIME_AVG"))
                        .data(rs.getString("DATA"))
                        .dataAvg(rs.getString("DATA_AVG"))
                        .totalTurb(rs.getFloat("TOTAL_TURB"))
                        .dataTotalDeep(rs.getString("DATA_TOTAL_DEEP"))
                        .dataDistance(rs.getString("DATA_DISTANCE"))
//                        .speedMax(rs.getLong("SPEED_MAX"))
//                        .deepAvg(rs.getLong("DEEP_AVG"))
//                        .deepMax(rs.getLong("DEEP_MAX"))
//                        .squareRiver(rs.getLong("SQUARE_RIVER"))
//                        .widthRiver(rs.getLong("WIDTH_RIVER"))
                        .waterFlow(rs.getFloat("WATER_FLOW"))
                        .suspendedMaterial(rs.getFloat("SUSPENDED_MATERIAL"))
                        .linkFile(rs.getString("LINK_FILE"))
                        .createdAt(rs.getDate("CREATED_AT"))
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

    @PostMapping("/create-liss")
    public DefaultResponseDTO createLISS(HttpServletRequest request, @RequestParam Map<String, String> params, @RequestParam("linkFile") MultipartFile[] file) throws SQLException, JsonProcessingException {
        log.info(objectMapper.writeValueAsString(params) +  file.length);

        loggerAction.info("{};{}", "create-liss", objectMapper.writeValueAsString(params));
        //tao thu muc luu tru file
        File f = new File(request.getRealPath("") + "/upload");
        if(!f.exists()){
            f.mkdir();
        }
        String linkFile = null;
        if(file[0].getSize() > 0) {
            linkFile = "upload/" + file[0].getOriginalFilename();
            String path = request.getRealPath("") + linkFile;
            f = new File(path);
            try (OutputStream os = new FileOutputStream(f)) {
                os.write(file[0].getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //luu cac thong tin con lai vao bang
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "insert into liss(ID,STATION_ID,TIME_START,TIME_END,TIME_AVG,DATA,DATA_AVG,DATA_TOTAL_DEEP,DATA_DISTANCE,TOTAL_TURB,CREATED_AT,CREATED_BY,LINK_FILE,SUSPENDED_MATERIAL,WATER_FLOW)\n" +
                "values(liss_seq.nextval,?,to_date(?,'dd/MM/yyyy HH24:MI'),to_date(?,'dd/MM/yyyy HH24:MI'),to_date(?,'dd/MM/yyyy HH24:MI'),?,?,?,?,?,sysdate,?,?,?,?)";
        try (Connection connection = ds.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
            statement.setString(i++, params.get("stationId"));
            statement.setString(i++, params.get("timeStart"));
            statement.setString(i++, params.get("timeEnd"));
            statement.setString(i++, params.get("timeAvg"));

            statement.setString(i++, params.get("data"));
            statement.setString(i++, params.get("dataAvg"));
            statement.setString(i++, params.get("dataTotalDeep"));
            statement.setString(i++, params.get("dataDistance"));
            statement.setString(i++, params.get("totalTurb"));
            statement.setString(i++, params.get("username"));
            if(linkFile != null) {
                statement.setString(i++, linkFile);
            }else{
                statement.setString(i++, null);
            }
            statement.setString(i++, params.get("suspendedMaterial"));
            statement.setString(i++, params.get("waterFlow"));
            statement.execute();

            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Thêm mới thất bại: " + e.getMessage());
            return defaultResponseDTO;
        }
    }

    @PostMapping("/update-liss")
    public DefaultResponseDTO updateLISS(HttpServletRequest request, @RequestParam Map<String, String> params, @RequestParam("linkFile") MultipartFile[] file) throws SQLException, JsonProcessingException {
        log.info(objectMapper.writeValueAsString(params) +  file.length);

        loggerAction.info("{};{}", "update-adcp", objectMapper.writeValueAsString(params));
        //tao thu muc luu tru file
        File f = new File(request.getRealPath("") + "/upload");
        if(!f.exists()){
            f.mkdir();
        }
        String linkFile = null;
        if(file[0].getSize() > 0) {
            linkFile = "upload/" + file[0].getOriginalFilename();
            String path = request.getRealPath("") + linkFile;
            f = new File(path);
            try (OutputStream os = new FileOutputStream(f)) {
                os.write(file[0].getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //luu cac thong tin con lai vao bang
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "update liss set TIME_START = to_date(?,'DD/MM/YYYY HH24:MI'),TIME_END = to_date(?,'DD/MM/YYYY HH24:MI'),TIME_AVG = to_date(?,'DD/MM/YYYY HH24:MI')" +
                ",DATA = ?,DATA_AVG = ?,DATA_TOTAL_DEEP = ?, DATA_DISTANCE = ?,TOTAL_TURB = ?,UPDATED_BY = ?,UPDATED_AT = sysdate,LINK_FILE = ?, SUSPENDED_MATERIAL= ?, WATER_FLOW = ?\n" +
                "where id =?";
        try (Connection connection = ds.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
//            statement.setString(i++, params.get("stationId"));
            statement.setString(i++, params.get("timeStart"));
            statement.setString(i++, params.get("timeEnd"));
            statement.setString(i++, params.get("timeAvg"));

            statement.setString(i++, params.get("data"));
            statement.setString(i++, params.get("dataAvg"));
            statement.setString(i++, params.get("dataTotalDeep"));
            statement.setString(i++, params.get("dataDistance"));
            statement.setString(i++, params.get("totalTurb"));
            statement.setString(i++, params.get("username"));
            if(linkFile != null) {
                statement.setString(i++, linkFile);
            }else{
                if(params.get("linkFileName") != null){
                    statement.setString(i++, params.get("linkFileName"));
                }else {
                    statement.setString(i++, null);
                }
            }
            statement.setString(i++, params.get("suspendedMaterial"));
            statement.setString(i++, params.get("waterFlow"));
            statement.setString(i++, params.get("id"));
            statement.execute();

            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Cập nhật thành công");
            return defaultResponseDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Cập nhật thất bại: " + e.getMessage());
            return defaultResponseDTO;
        }
    }

    @PostMapping("/delete-liss")
    public DefaultResponseDTO deleteLISS(@RequestParam("id") String id){

        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "delete liss where id =?";
        try (Connection connection = ds.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            int i = 1;
            statement.setString(i++, id);
            statement.execute();

            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
            return defaultResponseDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Xóa thất bại: " + e.getMessage());
            return defaultResponseDTO;
        }
    }
}
