package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.FileWaterLevelInfo;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WaterLevelExecutedVM;
import com.neo.nbdapi.rest.vm.WaterLevelVM;
import com.neo.nbdapi.services.WaterLevelService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CONFIG_WATER_LEVEL)
public class WaterLevelController {
    @Autowired
    private WaterLevelService waterLevelService;
    @PostMapping("/get-water-level")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return waterLevelService.getListWaterLevel(defaultRequestPagingVM);
    }

    @PostMapping("/update-water-level")
    public DefaultResponseDTO updateWaterLevel(@RequestBody WaterLevelVM waterLevelVM) throws SQLException {
        return waterLevelService.updateWaterLevel(waterLevelVM);
    }

    @PostMapping("/execute-water-level")
    public DefaultResponseDTO executeWaterLevel (@RequestBody WaterLevelExecutedVM waterLevelExecutedVM) throws SQLException, FileNotFoundException, ParseException {
        return waterLevelService.executeWaterLevel(waterLevelExecutedVM);
    }
    @GetMapping("/file-out-put-info")
    public List<FileWaterLevelInfo> getInfoFileWaterLevelInfo(){
        return waterLevelService.getInfoFileWaterLevelInfo();
    }
    @GetMapping("/file-guess-info")
    public List<FileWaterLevelInfo> getInfoFileWaterGuess(){
        return waterLevelService.getInfoFileGuess();
    }

}
