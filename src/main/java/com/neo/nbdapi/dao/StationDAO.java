package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.Station;
import com.neo.nbdapi.rest.vm.SelectVM;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.List;

public interface StationDAO {
    List<ComboBoxStr> getStationComboBox(String query) throws SQLException;

    List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException;

    // method cũ tạm thời chưa dùng
//    List<Object[]> getCSVAllStationOwnedByUser(String username) throws SQLException;

    // lay danh sach loai tram theo user va object type
    List<StationMapDTO> getAllStationOwnedByUserAndObjectType(String username, String objectType) throws SQLException;
    // lay danh sach loai tram theo user
    List<StationMapDTO> getAllStationOwnedByUser(String username) throws SQLException;

    Station findStationByStationCodeAndActiveAndIsdel(String stationCode) throws SQLException;

    boolean isStationOwnedByUser(String stationId, String userId) throws SQLException;

    // lấy danh sách loại trạm theo user và object type tra ve csv
    // method cu tam thoi khong dung
//    List<Object[]> getCSVAllStationOwnedByUserAndObjectType(String username, String objectType) throws SQLException;
}
