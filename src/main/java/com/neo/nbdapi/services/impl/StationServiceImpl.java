package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.StationMapDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.services.StationService;
import com.neo.nbdapi.utils.CsvUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationDAO stationDAO;

    @Override
    public List<ComboBoxStr> getStationComboBox(String query) throws SQLException {
        return stationDAO.getStationComboBox(query);
    }

    /**
     * API lấy tất cả các trạm của user
     * @return
     * @throws SQLException
     */
//    @Override
//    public String getAllStationCsv() throws SQLException {
//        String header = "stationId,stationCode,stationName,image,longitude,latitude,transMiss,address,areaName,isActive,stationTypeName";
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return CsvUtils.writeToCsvText(stationDAO.getAllStationOwnedByUser(username), header);
//    }

    // api cũ tạm thời chưa dùng
//    @Override
//    public String getCSVStationWithObjectType(String objectType) throws SQLException {
//        String header = "stationId,stationCode,stationName,image,longitude,latitude,transMiss,address,areaName,isActive,stationTypeName";
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return Strings.isEmpty(objectType) ? CsvUtils.writeToCsvText(stationDAO.getAllStationOwnedByUser(username), header) : CsvUtils.writeToCsvText(stationDAO.getAllStationOwnedByUserAndObjectType(username, objectType), header);
//    }

    /**
     * Lấy danh sách các trạm theo loại trạm hoặc là tất cả dựa vào objectType (loại trạm)
     * @param objectType
     * @return List<StationMapDTO>
     * @throws SQLException
     */
    @Override
    public List<StationMapDTO> getAllStationOwnedByUserAndObjectType(String objectType) throws SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Strings.isEmpty(objectType) ? stationDAO.getAllStationOwnedByUser(username) : stationDAO.getAllStationOwnedByUserAndObjectType(username, objectType);
    }

    @Override
    public List<ComboBoxStr> getStationByUser() {
        return stationDAO.getStationByUser();
    }

    @Override
    public List<ComboBoxStr> getStationComboBoxWaterLevel(String query) throws SQLException {
        return stationDAO.getStationComboBoxWaterLevel(query);
    }
}
