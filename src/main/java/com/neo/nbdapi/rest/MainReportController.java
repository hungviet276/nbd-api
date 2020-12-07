package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ADCP;
import com.neo.nbdapi.entity.ADCP2;
import com.neo.nbdapi.entity.LISS;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/report")
public class MainReportController {
    private final Logger logger = LogManager.getLogger(MainReportController.class);

    @Autowired
    private HikariDataSource ds;

    @GetMapping("/get-data-report")
    public ResponseEntity<List<Object>> getDataReport(@RequestParam Map<String, String> params) {
        List resultReport = new ArrayList();
        List<Float> valueList = new ArrayList<>();
        List<Float> trendList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "select CUR_TS_TYPE_ID from stations where STATION_ID = ?";
        String sql2 = "SELECT STORAGE,TS_ID from station_time_series where PARAMETERTYPE_ID = ?  and  STATION_ID = ?  and TS_TYPE_ID = ?";
        String sql3 = "select %s, TIMESTAMP from %s where timestamp >= TO_DATE(?, 'YYYY-MM-DD') and timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1";
        String sql4 = "select value, TIMESTAMP from rainfall_1h where timestamp >= TO_DATE('2020-11-23', 'YYYY-MM-DD') and timestamp < TO_DATE('2020-11-23', 'YYYY-MM-DD' ) +1";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(sql);
             PreparedStatement statement2 = connection.prepareStatement(sql2)

        ) {

            statement1.setString(1, params.get("stationId"));
            ResultSet rs1 = statement1.executeQuery();
            if (!rs1.isBeforeFirst()) {

            }
            rs1.next();
            Integer curTypeId = rs1.getInt("CUR_TS_TYPE_ID");
            if (curTypeId != null) {
                statement2.setInt(1, Integer.parseInt(params.get("parameterTypeId")));
                statement2.setString(2, params.get("stationId"));
                statement2.setInt(3, curTypeId);
                ResultSet rs2 = statement2.executeQuery();
                if (!rs2.isBeforeFirst()) {

                }
                rs2.next();
                String storage = rs2.getString("STORAGE");
                Integer tsId = rs2.getInt("TS_ID");
                if (storage != null && tsId != null) {
                    String table = storage + "_" + params.get("step");
                    sql3 = String.format(sql3, params.get("feature"), table);
                    PreparedStatement statement3 = connection.prepareStatement(sql3);
                    statement3.setString(1, params.get("endDate"));
                    statement3.setString(2, params.get("startDate"));
                    ResultSet rs3 = statement3.executeQuery();
                    while (rs3.next()) {
                        Float value = rs3.getFloat(params.get("feature"));
                        String timestamp = rs3.getString("TIMESTAMP");
                        valueList.add(value);
                        timeList.add(timestamp);
                    }
                    // Tinh toan cua so truot
                    List<Float> newList = new ArrayList<>();
                    for (int i = 0; i < valueList.size() - Integer.parseInt(params.get("trend")) + 1; i++) {
                        for (int j = 0; j < Integer.parseInt(params.get("trend")); j++) {
                            newList.add(valueList.get(i + j));
                        }
                        trendList.add(getAverage(newList));
                        newList.clear();
                    }
                    statement3.close();
                }
            }
            resultReport.add(valueList);
            resultReport.add(trendList);
            resultReport.add(timeList);
            connection.commit();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(resultReport, HttpStatus.OK);
    }

    static Float getAverage(List<Float> valueList) {
        float sumValue = 0;
        float avgValue = 0;
        for (int i = 0; i < valueList.size(); i++) {
            sumValue += valueList.get(i);
        }
        avgValue = sumValue / valueList.size();
        return avgValue;
    }

    @GetMapping("/get-data-report-hydrological")
    public ResponseEntity<List<Object>> getDataReportHydrological(@RequestParam Map<String, String> params) {
        List<Object> resultReport = new ArrayList();
        List<ADCP2> adcpList = new ArrayList<>();
        List<LISS> lissList = new ArrayList<>();

        String sql1 = "select a.*, b.STATION_NAME from adcp a , stations b  where a.TIME_START >= TO_DATE(?, 'YYYY-MM-DD') and a.TIME_END <= TO_DATE(?, 'YYYY-MM-DD' )+1 and a.STATION_ID = ? and a.STATION_ID = b.STATION_ID ";
        String sql2 = "select a.*, b.STATION_NAME from liss a , stations b  where a.TIME_START >= TO_DATE(?, 'YYYY-MM-DD') and a.TIME_END <= TO_DATE(?, 'YYYY-MM-DD' )+1 and a.STATION_ID = ? and a.STATION_ID = b.STATION_ID ";
        String sql3 = "select a.*, b.STATION_NAME from adcp a , stations b  where a.TIME_START >= TO_DATE('2020-11-23', 'YYYY-MM-DD') and a.TIME_END <= TO_DATE('2020-12-23', 'YYYY-MM-DD' )+1 and a.STATION_ID = '9_58_474_428' and a.STATION_ID = b.STATION_ID ;";
        if(params.get("rowNum") != null){
            sql1 += "and ROWNUM <=7";
            sql2 += "and ROWNUM <=7";
        }
        try (Connection connection = ds.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(sql1);
             PreparedStatement statement2 = connection.prepareStatement(sql2)

        ) {

            statement1.setString(1, params.get("startDate"));
            statement1.setString(2, params.get("endDate"));
            statement1.setString(3, params.get("stationId"));
            ResultSet rs1 = statement1.executeQuery();
            while (rs1.next()) {
                ADCP2 adcp = new ADCP2().builder()
                        .stationId(rs1.getString("STATION_ID"))
                        .timeStart(rs1.getDate("TIME_START"))
                        .timeEnd(rs1.getDate("TIME_END"))
                        .timeAvg(rs1.getDate("TIME_AVG"))
                        .waterLevelStart(rs1.getLong("WATER_LEVEL_START"))
                        .waterLevelEnd(rs1.getLong("WATER_LEVEL_END"))
                        .waterLevelAvg(rs1.getLong("WATER_LEVEL_AVG"))
                        .speedAvg(rs1.getFloat("SPEED_AVG"))
                        .speedMax(rs1.getFloat("SPEED_MAX"))
                        .deepAvg(rs1.getFloat("DEEP_AVG"))
                        .deepMax(rs1.getFloat("DEEP_MAX"))
                        .squareRiver(rs1.getFloat("SQUARE_RIVER"))
                        .widthRiver(rs1.getFloat("WIDTH_RIVER"))
                        .waterFlow(rs1.getFloat("WATER_FLOW"))
                        .note(rs1.getString("NOTE"))
                        .createBy(rs1.getString("CREATED_BY"))
                        .updateBy(rs1.getString("UPDATED_BY"))
                        .linkFile(rs1.getString("LINK_FILE"))
                        .createdAt(rs1.getDate("CREATED_AT"))
                        .measureNTH(rs1.getLong("MEASURE_NTH"))
                        .updateAt(rs1.getDate("UPDATED_AT"))
                        .stationName(rs1.getString("STATION_NAME"))
                        .build();
                adcpList.add(adcp);

            }
            statement2.setString(1, params.get("startDate"));
            statement2.setString(2, params.get("endDate"));
            statement2.setString(3, params.get("stationId"));
            ResultSet rs2 = statement2.executeQuery();
            while (rs2.next()) {
                LISS liss = new LISS().builder()
                        .stationId(rs2.getString("STATION_ID"))
                        .createBy(rs2.getString("CREATED_BY"))
                        .totalTurb(rs2.getFloat("TOTAL_TURB"))
                        .updateBy(rs2.getString("UPDATED_BY"))
                        .timeStart(rs2.getDate("TIME_START"))
                        .timeEnd(rs2.getDate("TIME_END"))
                        .timeAvg(rs2.getDate("TIME_AVG"))
                        .updateAt(rs2.getDate("UPDATED_AT"))
                        .data(rs2.getString("DATA"))
                        .dataAvg(rs2.getString("DATA_AVG"))
                        .linkFile(rs2.getString("LINK_FILE"))
                        .createdAt(rs2.getDate("CREATED_AT"))
                        .dataTotalDeep(rs2.getString("DATA_TOTAL_DEEP"))
                        .dataDistance(rs2.getString("DATA_DISTANCE"))
                        .suspendedMaterial(rs2.getLong("SUSPENDED_MATERIAL"))
                        .waterFlow(rs2.getFloat("WATER_FLOW"))
                        .stationName(rs2.getString("STATION_NAME"))
                        .build();
                lissList.add(liss);

            }
            statement1.close();
            statement2.close();

            resultReport.add(adcpList);
            resultReport.add(lissList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(resultReport,HttpStatus.OK);
    }

}
