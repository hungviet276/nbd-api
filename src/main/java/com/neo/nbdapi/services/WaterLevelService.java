package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.FileWaterLevelInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface WaterLevelService {
    DefaultPaginationDTO getListWaterLevel(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    DefaultResponseDTO updateWaterLevel(WaterLevelVM waterLevelVM) throws SQLException;
    DefaultResponseDTO executeWaterLevel(WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException, FileNotFoundException, ParseException;
    List<FileWaterLevelInfo> getInfoFileWaterLevelInfo();
    List<FileWaterLevelInfo> getInfoFileGuess();
    DefaultResponseDTO executeGuess( String stationId ,  Integer end, Integer start, MultipartFile file, String type) throws IOException;
    ResponseEntity<InputStreamResource> downloadTemplate(HttpServletRequest request) throws IOException, BusinessException;

}
