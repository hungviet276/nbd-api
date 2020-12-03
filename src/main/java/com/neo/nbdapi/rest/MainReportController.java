package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultResponseDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String sql3 = "select ?, TIMESTAMP from ? where timestamp < TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF') and timestamp > TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF')";
        String sql4 = "select VALUE, TIMESTAMP from temperature_1h where timestamp < TO_TIMESTAMP('2020-11-21 12:00:00', 'YYYY-MM-DD HH24:MI:SS.FF') and timestamp > TO_TIMESTAMP('2020-11-21 9:00:00', 'YYYY-MM-DD HH24:MI:SS.FF')";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(sql);
             PreparedStatement statement2 = connection.prepareStatement(sql2);
             PreparedStatement statement3 = connection.prepareStatement(sql3)) {

            statement1.setString(1, params.get("stationId"));
            ResultSet rs1 = statement1.executeQuery();

            rs1.next();
            Integer curTypeId = rs1.getInt("CUR_TS_TYPE_ID");
            if (curTypeId != null) {
//                sql2 = String.format(sql2, curTypeId);
                statement2.setInt(1, Integer.parseInt(params.get("parameterTypeId")));
                statement2.setString(2, params.get("stationId"));
                statement2.setInt(3, curTypeId);
                ResultSet rs2 = statement2.executeQuery();
                rs2.next();
                String storage = rs2.getString("STORAGE");
                Integer tsId = rs2.getInt("TS_ID");
                if (storage != null && tsId != null) {
                    String table = storage + "_" + params.get("step");
                    statement3.setString(1, params.get("feature"));
                    statement3.setString(2, table);
                    statement3.setString(3, params.get("startDate"));
                    statement3.setString(4, params.get("endDate"));
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

                }
            }
            resultReport.add(valueList);
            resultReport.add(trendList);
            resultReport.add(timeList);
            connection.commit();

        } catch (Exception e) {

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
}
