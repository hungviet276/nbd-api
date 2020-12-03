package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBoxStr;

import java.sql.SQLException;
import java.util.List;

public interface StationService {
    List<ComboBoxStr> getStationComboBox(String query) throws SQLException;
    List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException;

//    String getAllStationCsv() throws SQLException;

    List<ComboBoxStr> getStationByUser();

    String getStationWithObjectType(String objectType) throws SQLException;
    // lấy danh sách các trạm theo loại trạm (objectType) hoặc là lấy tất cả theo csv
    // api cũ tạm thời không dùng
//    String getCSVStationWithObjectType(String objectType) throws SQLException;

    // lấy danh sách các trạm theo loại trạm hoặc là lấy tất cả nếu objectType NULL, json
    List<StationMapDTO>  getAllStationOwnedByUserAndObjectType(String objectType) throws SQLException;
}
