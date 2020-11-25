package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.WaterLevelDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.*;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;
import com.neo.nbdapi.services.WaterLevelService;
import com.neo.nbdapi.services.objsearch.WaterLevelSearch;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static Long timeTmp;

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
                if(objectSearch.getHours() != null){
                    if(objectSearch.getHours() ==1){
                        sql.append(" and TO_CHAR(TIMESTAMP,'MI')='00' ");
                    } else if(objectSearch.getHours() ==3){
                        sql.append(" and TO_CHAR(TIMESTAMP,'MI')='00' ");
                        sql.append(" and MOD(to_number(TO_CHAR(TIMESTAMP,'HH')),3) = 0 ");
                    } else if(objectSearch.getHours() ==24){
                        sql.append(" and TO_CHAR(TIMESTAMP,'MI')='00' ");
                        sql.append(" and TO_CHAR(TIMESTAMP,'HH24')='00' ");
                    }
                }
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
        if(waterLevelVM.getValue() < variableTime.getMin() && variableTime.getMin()!=0 && variableTime.getMax()!=0){
            waterLevelVM.setWarning(2);
            continude = true;
        }

        if(waterLevelVM.getValue() > variableTime.getMax() && !continude && variableTime.getMin()!=0 && variableTime.getMax()!=0){
            waterLevelVM.setWarning(3);
            continude = true;
            //update và return
        }

        if(nearest!=null){
            if(Math.abs(waterLevelVM.getValue() - nearest) > variableTime.getVariableTime() && !continude && variableTime.getMin()!=0 && variableTime.getMax()!=0){
                waterLevelVM.setWarning(4);
                continude = true;
            }
        }

        if(!continude){
            for(VariablesSpatial variablesSpatial : variablesSpatials){
                if(variablesSpatial.getMin() - waterLevelVM.getValue() > variableTime.getVariableSpatial()){
                    waterLevelVM.setWarning(5);
                    break;
                } else if(waterLevelVM.getValue() - variablesSpatial.getMax() > variableTime.getVariableSpatial()&& variablesSpatial.getMax()>0){
                    waterLevelVM.setWarning(5);
                    break;
                }
            }
        }
        if(!continude){
            waterLevelVM.setWarning(1);
        }
        return waterLevelDAO.updateWaterLevel(waterLevelVM);
    }

    @Override
    public DefaultResponseDTO executeWaterLevel(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException {
            String fileName = "/";
            String fileNameExecute = "/";
        if(waterLevelExecutedVM.getStationId().equals(Constants.WATER_LEVEL.ID_PHU_QUOC)){
            fileName+=Constants.WATER_LEVEL.FILE_PHU_QUOC;
            fileNameExecute=Constants.WATER_LEVEL.FILE_PHU_QUOC;
        } else if(waterLevelExecutedVM.getStationId().equals(Constants.WATER_LEVEL.ID_GANH_HAO)){
            fileName+=Constants.WATER_LEVEL.FILE_GANH_HAO;
            fileNameExecute=Constants.WATER_LEVEL.FILE_GANH_HAO;
        } else if(waterLevelExecutedVM.getStationId().equals(Constants.WATER_LEVEL.ID_HA_TIEN)){
            fileName+=Constants.WATER_LEVEL.FILE_HA_TIEN;
            fileNameExecute=Constants.WATER_LEVEL.FILE_HA_TIEN;;
        } else{
            return DefaultResponseDTO.builder().status(0).message("Trạm chưa hợp lệ").build();
        }


        if(waterLevelExecutedVM.getHours() == 1){
            fileName+="1h";
            fileNameExecute+="1h";
        } else if(waterLevelExecutedVM.getHours() == 3){
            fileName+="3h";
            fileNameExecute+="3h";
        }
        else if(waterLevelExecutedVM.getHours() == 24){
            fileName+="24h";
            fileNameExecute+="24h";
        } else{
            return DefaultResponseDTO.builder().status(0).message("Khoảng thời gian chưa hợp lệ").build();
        }
        this.timeTmp = 0L;

        List<WaterLevelExecute> waterLevels = waterLevelDAO.executeWaterLevel(waterLevelExecutedVM);
        if(waterLevels.size() == 0){
            return DefaultResponseDTO.builder().status(1).message("Thành công").build();
        }

        try{
            String folderExport = Constants.WATER_LEVEL.FOLDER_EXPORT;



            PrintWriter print = new PrintWriter(new File(folderExport + fileName + ".ip"));

            WaterLevelExecute firstTmp = waterLevels.get(0);

            Calendar calendarFirst = convertStringToCalender(firstTmp);

            StringBuilder title = new StringBuilder("     1 ");
            title.append(calendarFirst.get(Calendar.YEAR));
            print.println(title.toString());

            for (WaterLevelExecute waterLevelExecute: waterLevels) {
                int position = waterLevels.indexOf(waterLevelExecute);
                if(position==0){
                    print.println(lineWithDate(waterLevelExecute, null));
                } else {
                    WaterLevelExecute waterLevelExecuteBefore = waterLevels.get(position-1);
                    if(convertStringToCalender(waterLevelExecute).get(Calendar.DAY_OF_MONTH) !=  convertStringToCalender(waterLevelExecuteBefore).get(Calendar.DAY_OF_MONTH)){
                        print.println(lineWithDate(waterLevelExecute, waterLevelExecuteBefore));
                    } else{
                        print.println(line(waterLevelExecute, waterLevelExecuteBefore));
                    }

                }

            }
            print.flush();
            print.close();

            // sử dụng restemplate để thực hiện tính hằng số điều hòa

            String command = "echo "+fileNameExecute+".par | ./tt_phantich_v1_2";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> map = new HashMap<>();
            map.put("commandExecute", command);
            map.put("stationId", waterLevelExecutedVM.getStationId());
            map.put("fileName", fileNameExecute+".ip");

            // build the request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            //DataResponse dataResponse = restTemplate.getForObject("http://localhost:8082/water-level/excute", DataResponse.class);

            ResponseEntity<String> response = restTemplate.postForEntity("http://192.168.1.20:8082/water-level/excute", entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("========================================>");
                logger.info("========================================>");
                logger.info("========================================> {}", response.getBody());
            } else {
                logger.info("========================================>");
                logger.info("========================================>");
                logger.info("========================================>{}", response.getStatusCode());
            }

        }
         catch (IOException | ParseException e) {
            logger.error("WaterLevelServiceImpl exception : {} ", e.getMessage());
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    private String lineWithDate(WaterLevelExecute waterLevelExecute, WaterLevelExecute waterLevelExecuteBefore) throws ParseException {
        Calendar calendar = convertStringToCalender(waterLevelExecute);
        if(waterLevelExecuteBefore != null){
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            Calendar calendarBefore = convertStringToCalender(waterLevelExecuteBefore);
            long tmp = calendar.getTimeInMillis() - calendarBefore.getTimeInMillis();
            this.timeTmp = this.timeTmp + tmp/1000;

        }
        StringBuilder line = new StringBuilder("");
        line.append(timeTmp+". ");
        line.append(waterLevelExecute.getValue());
        line.append(" \t");
        line.append(calendar.get(Calendar.YEAR));
        line.append(" ");
        line.append(calendar.get(Calendar.MONTH)+1);
        line.append(" ");
        line.append(calendar.get(Calendar.DAY_OF_MONTH));
        line.append(" ");
        line.append(calendar.get(Calendar.HOUR));
        line.append(" ");
        line.append(calendar.get(Calendar.MINUTE));
        line.append(" ");
        line.append(calendar.get(Calendar.SECOND));
        return line.toString();
    }
    private String line(WaterLevelExecute waterLevelExecute, WaterLevelExecute waterLevelExecuteBefore) throws ParseException {
        Calendar calendar = convertStringToCalender(waterLevelExecute);
        if(waterLevelExecuteBefore != null){
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            Calendar calendarBefore = convertStringToCalender(waterLevelExecuteBefore);
            long tmp = calendar.getTimeInMillis() - calendarBefore.getTimeInMillis();
            this.timeTmp = this.timeTmp + tmp/1000;

        }
        StringBuilder line = new StringBuilder("");
        line.append(timeTmp+". ");
        line.append(waterLevelExecute.getValue());
        return line.toString();
    }
    private Calendar convertStringToCalender(WaterLevelExecute tmp) throws  ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date dateFirst = formatter.parse(tmp.getTimestamp());
        Calendar calendarFirst = Calendar.getInstance();
        calendarFirst.setTime(dateFirst);
        return calendarFirst;

    }
}