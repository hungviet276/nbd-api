package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ADCP;
import com.neo.nbdapi.entity.ADCP2;
import com.neo.nbdapi.entity.LISS;
import com.neo.nbdapi.entity.ObjectValue;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        List<Float> minList = new ArrayList<>();
        List<Float> maxList = new ArrayList<>();
        List<Float> avgList = new ArrayList<>();
        List<Float> totalList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();

        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "select CUR_TS_TYPE_ID,STATION_NAME from stations where STATION_ID = ?";
        String sql2 = "SELECT STORAGE,TS_ID from station_time_series where PARAMETERTYPE_ID = ?  and  STATION_ID = ?  and TS_TYPE_ID = ?";
        String sql3 = "select VALUE,AVG_VALUE,MIN_VALUE,MAX_VALUE,TOTAL_VALUE, TIMESTAMP from %s where timestamp >= TO_DATE(?, 'YYYY-MM-DD') and timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1";
        String sql4 = "select %s, TIMESTAMP from %s where timestamp >= TO_DATE(?, 'YYYY-MM-DD') and timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1";
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
            String stationName = rs1.getString("STATION_NAME");
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
                    sql3 = String.format(sql3, table);
//                    sql3 = String.format(sql3, params.get("feature"), table);
                    PreparedStatement statement3 = connection.prepareStatement(sql3);
                    statement3.setString(1, params.get("startDate"));
                    statement3.setString(2, params.get("endDate"));
                    ResultSet rs3 = statement3.executeQuery();
                    while (rs3.next()) {
                        Float value = rs3.getFloat("VALUE");
                        Float min = rs3.getFloat("MIN_VALUE");
                        Float max = rs3.getFloat("MAX_VALUE");
                        Float avg = rs3.getFloat("AVG_VALUE");
                        Float total = rs3.getFloat("TOTAL_VALUE");

                        String timestamp = rs3.getString("TIMESTAMP");
                        valueList.add(value);
                        minList.add(min);
                        maxList.add(max);
                        avgList.add(avg);
                        totalList.add(total);
                        timeList.add(timestamp);
                    }
                    // Tinh toan cua so truot
//                    List<Float> newList = new ArrayList<>();
//                    for (int i = 0; i < valueList.size() - Integer.parseInt(params.get("trend")) + 1; i++) {
//                        for (int j = 0; j < Integer.parseInt(params.get("trend")); j++) {
//                            newList.add(valueList.get(i + j));
//                        }
//                        trendList.add(getAverage(newList));
//                        newList.clear();
//                    }
                    resultReport.add(valueList);
                    resultReport.add(minList);
                    resultReport.add(maxList);
                    resultReport.add(avgList);
                    resultReport.add(totalList);
                    resultReport.add(timeList);
                    resultReport.add(stationName);
                    if (valueList.size() >= Integer.parseInt(params.get("trend"))) {
                        switch (params.get("feature")) {
                            case "value":
                                resultReport.add(getTrendList(valueList, params.get("trend")));
                                break;
                            case "avg_value":
                                resultReport.add(getTrendList(avgList, params.get("trend")));
                                break;
                            case "min_value":
                                resultReport.add(getTrendList(minList, params.get("trend")));
                                break;
                            case "max_value":
                                resultReport.add(getTrendList(maxList, params.get("trend")));
                                break;
                            case "sum_value":
                                resultReport.add(getTrendList(totalList, params.get("trend")));
                                break;
                        }
                    }

                    statement3.close();
                }
            }

            connection.commit();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(resultReport, HttpStatus.OK);
    }

    static List<Float> getTrendList(List<Float> valueList, String trend) {
        List<Float> trendList = new ArrayList<>();
        List<Float> newList = new ArrayList<>();
        for (int i = 0; i < valueList.size() - Integer.parseInt(trend) + 1; i++) {
            for (int j = 0; j < Integer.parseInt(trend); j++) {
                newList.add(valueList.get(i + j));
            }
            trendList.add(getAverage(newList));
            newList.clear();
        }
        return trendList;
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

    @GetMapping("/get-data-prediction")
    public ResponseEntity<List<Object>> getDataPredictionReport(@RequestParam Map<String, String> params) {
        List resultReport = new ArrayList();
        List<Float> valueList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();

        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        String sql = "select VALUE , PREDICTION_TIME from TIDAL_PREDICTION where prediction_time >= TO_DATE(?,'dd-MON-yy')and prediction_time <= TO_DATE(?,'dd-MON-yy')+1 and STATION_ID =?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(sql)
        ) {
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd");
            Date startTime1 = formatter.parse(startDate);
            Date endTime1 = formatter.parse(endDate);
            DateFormat df = new SimpleDateFormat("dd-MMM-yy");
            String startTime = df.format(startTime1);
            String endTime = df.format(endTime1);
//            String startDate = params.get("startDate");
//            statement1.setString(1, params.get("startDate"));
//            statement1.setString(2, params.get("endDate"));
//            statement1.setString(3, params.get("stationId"));
            statement1.setString(1, String.valueOf(startTime));
            statement1.setString(2, String.valueOf(endTime));
            statement1.setString(3, params.get("stationId"));
            ResultSet rs1 = statement1.executeQuery();

            while (rs1.next()) {
                Float value = rs1.getFloat("VALUE");
                String time = rs1.getString("PREDICTION_TIME");
                valueList.add(value);
                timeList.add(time);
            }
            resultReport.add(valueList);
            resultReport.add(timeList);
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(resultReport, HttpStatus.OK);
    }


    @GetMapping("/get-data-report-multiple-element")
    public ResponseEntity<List<Object>> getDataReportMultipleElment(@RequestParam Map<String, String> params) throws SQLException {
        List resultReport = new ArrayList();

        Map<Float, String> valueList = new HashMap<>();
        Map<Float, String> minList = new HashMap<>();
        Map<Float, String> maxList = new HashMap<>();
        Map<Float, String> avgList = new HashMap<>();
        Map<Float, String> totalList = new HashMap<>();
        Map<Date, String> timeList = new HashMap<>();
        List<ObjectValue> result = new ArrayList<>();

        String stationName = "";

        String sql = "SELECT STORAGE,TS_ID,STATION_NAME from station_time_series where STATION_ID = ? ";
//        String sql3 = "select VALUE,AVG_VALUE,MIN_VALUE,MAX_VALUE,TOTAL_VALUE, TIMESTAMP from %s where timestamp >= TO_DATE(?, 'YYYY-MM-DD') and timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1 ";
//        String sql4 = "select VALUE,AVG_VALUE,MIN_VALUE,MAX_VALUE,TOTAL_VALUE, TIMESTAMP from %s where timestamp >= TO_DATE(?, 'YYYY-MM-DD') and timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1 and TS_ID = ?";
        String sql5 = "";
        PreparedStatement st = null;
        try (Connection connection = ds.getConnection()) {
            String[] listParTypeId = params.get("parameterTypeId").split(",");
            String append = " and PARAMETERTYPE_ID in (";
            for (int i = 0; i < listParTypeId.length; i++) {
                append += "?,";
            }
            append += "-1) order by STORAGE";
            sql += append;
            st = connection.prepareStatement(sql);

            st.setString(1, params.get("stationId"));
            for (int i = 0; i < listParTypeId.length; i++) {
                st.setInt(i + 2, Integer.parseInt(listParTypeId[i]));
            }
            ResultSet rs = st.executeQuery();

            Map<String, List<Integer>> storageList = new HashMap<>();
            if (rs.isBeforeFirst()) {
                Integer tsId = null;
                String storage = "";
                String storage2 = "";
                List<Integer> list = new ArrayList<>();
                while (rs.next()) {
                    storage2 = rs.getString("STORAGE");
                    tsId = rs.getInt("TS_ID");
                    if (storage.equals("") || storage.equals(storage2)) {
                        storage = rs.getString("STORAGE");
                        list.add(tsId);
                    } else {

                        storageList.put(storage, list);
                        list = new ArrayList<>();
                        list.add(tsId);
                        storage = storage2;
                    }
                }
                list.add(tsId);
                storageList.put(storage, list);
                //duyet cac storage noi voi step de lay ra du lieu
                sql = "";
                for (Map.Entry item : storageList.entrySet()) {
                    sql += "select timestamp,TS_ID, VALUE, AVG_VALUE, MIN_VALUE, MAX_VALUE, TOTAL_VALUE, '" + item.getKey() + "'TBL_NAME from " + item.getKey() + "_" + params.get("step");
                    sql += " where timestamp >= to_date('" + params.get("startDate") + "','YYYY-MM-DD') and timestamp <= to_date('" + params.get("endDate") + "','YYYY-MM-DD') + 1 and TS_ID in (";
                    list = (List<Integer>) item.getValue();
                    for (Integer i : list) {
                        sql += i + ", ";
                    }
                    sql += "-1) union ";
                }
                //xoa cai union cuoi cung di
                sql = sql.substring(0, sql.lastIndexOf(" union "));
//                sql += " GROUP BY STORAGE";
                st = connection.prepareStatement(sql);
                System.out.println("Chuoi query : " + sql);
                rs = st.executeQuery();
                while (rs.next()) {
                    ObjectValue bo = ObjectValue.builder()
                            .minValue(rs.getFloat("MIN_VALUE"))
                            .maxValue(rs.getFloat("MAX_VALUE"))
                            .avgValue(rs.getFloat("AVG_VALUE"))
                            .totalValue(rs.getFloat("TOTAL_VALUE"))
                            .timestamp(rs.getDate("TIMESTAMP"))
                            .tblName(rs.getString("TBL_NAME"))
                            .value(rs.getFloat("VALUE"))
                            .build();

                    result.add(bo);
//                    valueList.put(bo.getAvgValue(), bo.getTblName());
//                    minList.put(bo.getMinValue(), bo.getTblName());
//                    maxList.put(bo.getMaxValue(), bo.getTblName());
//                    avgList.put(bo.getAvgValue(), bo.getTblName());
//                    totalList.put(bo.getTotalValue(), bo.getTblName());
//                    timeList.put(bo.getTimestamp(), bo.getTblName());

                }
                resultReport.add(result);
//                resultReport.add(minList);
//                resultReport.add(maxList);
//                resultReport.add(avgList);
//                resultReport.add(totalList);
//                resultReport.add(timeList);
            }
//                if (listParTypeId.length == 2) {
//                    sql5 = "select a.*,b.* from %s a, %s b where a.timestamp >= TO_DATE(?, 'YYYY-MM-DD') and a.timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1 and a.timestamp = b.timestamp";
//                } else if (listParTypeId.length == 6) {
//                    sql5 = "select a.*,b.*,c*,d*,e*,f* from %s a, %s b, %s c, %s d,%s e,%s f where a.timestamp >= TO_DATE(?, 'YYYY-MM-DD') and a.timestamp < TO_DATE(?, 'YYYY-MM-DD' ) +1 and a.timestamp = b.timestamp" +
//                            "and a.timestamp = c.timestamp and a.timestamp = d.timestamp and a.timestamp = e.timestamp and a.timestamp = f.timestamp";
//                }
//
//
//                if (storageList.entrySet() != null && tsId != null) {
//                    String table = storage + "_" + params.get("step");
//                    sql3 = String.format(sql3, table);
////                    sql3 = String.format(sql3, params.get("feature"), table);
//                    PreparedStatement statement3 = connection.prepareStatement(sql3);
//                    statement3.setString(1, params.get("startDate"));
//                    statement3.setString(2, params.get("endDate"));
//                    statement3.setInt(3, tsId);
//                    ResultSet rs3 = statement3.executeQuery();
//                    while (rs3.next()) {
//                        Float value = rs3.getFloat("VALUE");
//                        Float min = rs3.getFloat("MIN_VALUE");
//                        Float max = rs3.getFloat("MAX_VALUE");
//                        Float avg = rs3.getFloat("AVG_VALUE");
//                        Float total = rs3.getFloat("TOTAL_VALUE");
//                        stationName = rs3.getString("STATION_NAME");
//
//                        String timestamp = rs3.getString("TIMESTAMP");
//                        valueList.add(value);
//                        minList.add(min);
//                        maxList.add(max);
//                        avgList.add(avg);
//                        totalList.add(total);
//                        timeList.add(timestamp);
//                    }
//                    // Tinh toan cua so truot
////                    List<Float> newList = new ArrayList<>();
////                    for (int i = 0; i < valueList.size() - Integer.parseInt(params.get("trend")) + 1; i++) {
////                        for (int j = 0; j < Integer.parseInt(params.get("trend")); j++) {
////                            newList.add(valueList.get(i + j));
////                        }
////                        trendList.add(getAverage(newList));
////                        newList.clear();
////                    }
//                    resultReport.add(valueList);
//                    resultReport.add(minList);
//                    resultReport.add(maxList);
//                    resultReport.add(avgList);
//                    resultReport.add(totalList);
//                    resultReport.add(timeList);
//                    resultReport.add(stationName);
//                    if (valueList.size() >= Integer.parseInt(params.get("trend"))) {
//                        switch (params.get("feature")) {
//                            case "value":
//                                resultReport.add(getTrendList(valueList, params.get("trend")));
//                                break;
//                            case "avg_value":
//                                resultReport.add(getTrendList(avgList, params.get("trend")));
//                                break;
//                            case "min_value":
//                                resultReport.add(getTrendList(minList, params.get("trend")));
//                                break;
//                            case "max_value":
//                                resultReport.add(getTrendList(maxList, params.get("trend")));
//                                break;
//                            case "sum_value":
//                                resultReport.add(getTrendList(totalList, params.get("trend")));
//                                break;
//                        }
//                    }
//
//                    statement3.close();
//                }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return new ResponseEntity<>(resultReport, HttpStatus.OK);
    }

    @GetMapping("/get-data-report-hydrological")
    public ResponseEntity<List<Object>> getDataReportHydrological(@RequestParam Map<String, String> params) {
        List<Object> resultReport = new ArrayList();
        List<ADCP2> adcpList = new ArrayList<>();
        List<LISS> lissList = new ArrayList<>();

        String sql1 = "select a.*, b.STATION_NAME from adcp a , stations b  where a.TIME_START >= TO_DATE(?, 'YYYY-MM-DD') and a.TIME_END <= TO_DATE(?, 'YYYY-MM-DD' )+1 and a.STATION_ID = ? and a.STATION_ID = b.STATION_ID ";
        String sql2 = "select a.*, b.STATION_NAME from liss a , stations b  where a.TIME_START >= TO_DATE(?, 'YYYY-MM-DD') and a.TIME_END <= TO_DATE(?, 'YYYY-MM-DD' )+1 and a.STATION_ID = ? and a.STATION_ID = b.STATION_ID ";
        String sql3 = "select a.*, b.STATION_NAME from adcp a , stations b  where a.TIME_START >= TO_DATE('2020-11-23', 'YYYY-MM-DD') and a.TIME_END <= TO_DATE('2020-12-23', 'YYYY-MM-DD' )+1 and a.STATION_ID = '9_58_474_428' and a.STATION_ID = b.STATION_ID ;";
        if (params.get("rowNum") != null) {
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
        return new ResponseEntity<>(resultReport, HttpStatus.OK);
    }

}
