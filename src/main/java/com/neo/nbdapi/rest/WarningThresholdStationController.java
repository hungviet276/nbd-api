package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.WarningThreshold;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.WarningThresholdValueVM;
import com.neo.nbdapi.services.WarningThresholdStationService;
import com.neo.nbdapi.services.WarningThresoldService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CONFIG_WARNING_THRESHOLD_STATION)
public class WarningThresholdStationController {
    @Autowired
    private WarningThresholdStationService warningThresholdStationService;

    @Autowired
    private WarningThresoldService warningThresoldService;

    @PostMapping("/get-list-warning-threshold-station")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return warningThresholdStationService.getListWarningThresholdStation(defaultRequestPagingVM);
    }

    @GetMapping("/duplicate-code-threshold-warning")
    public DefaultResponseDTO getListMailConfigPagination(@RequestParam String code) throws SQLException, BusinessException {
        return warningThresoldService.getDuplicateCodeWarningThreshold(code);
    }

    @PostMapping
    public DefaultResponseDTO createWarningThreshold(@RequestBody WarningThresholdValueVM warningThresholdValueVM) throws SQLException {
        return warningThresholdStationService.createWarningThreshold(warningThresholdValueVM);
    }
    @PutMapping
    public DefaultResponseDTO editWarningThreshold(@RequestBody WarningThresholdValueVM warningThresholdValueVM) throws SQLException {
        return warningThresholdStationService.editWarningThreshold(warningThresholdValueVM);
    }

    @GetMapping("/warning-thresholds")
    public List<WarningThreshold> getWarningThresholds(@RequestParam Long thresholdValueId) throws SQLException {
        return warningThresoldService.getWarningThresholds(thresholdValueId);
    }

    @GetMapping("/get-parameter-select")
    public ComboBox getValueType(@RequestParam Long id) throws SQLException {
        return warningThresholdStationService.getValueType(id);
    }
    @DeleteMapping
    public DefaultResponseDTO deleteWarningThresholdValue(@RequestParam Long id) throws SQLException {
        return warningThresholdStationService.deleteWarningThresholdValue(id);
    }

}
