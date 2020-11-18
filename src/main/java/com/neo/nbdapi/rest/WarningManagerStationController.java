package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.WarningManagerStationDTO;
import com.neo.nbdapi.dto.WarningMangerDetailInfoDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.entity.WarningThresholdINF;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerStrVM;
import com.neo.nbdapi.rest.vm.SelectWarningManagerVM;
import com.neo.nbdapi.services.WarningMangerStationService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CONFIG_WARNING_MANAGER_STATION)
public class WarningManagerStationController {

    @Autowired
    private WarningMangerStationService warningMangerStationService;

    @PostMapping("/get-list-warning-manager-station")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return warningMangerStationService.getListWarningThresholdStation(defaultRequestPagingVM);
    }
    @PostMapping("/get-list-param-warning-config")
    public List<ComboBox> getListParameterWarningConfig(@RequestBody @Valid SelectWarningManagerStrVM selectVM) throws SQLException{
        return warningMangerStationService.getListParameterWarningConfig(selectVM);
    }
    @PostMapping("/get-list-warning-config-threshold")
    public List<ComboBox> getListParameterWarningThreshold(@RequestBody @Valid SelectWarningManagerVM selectVM) throws SQLException{
        return warningMangerStationService.getListParameterWarningThreshold(selectVM);
    }
    @GetMapping("/get-info-warning-threshold")
    WarningThresholdINF getInFoWarningThreshold(@RequestParam Long idThreshold) throws SQLException{
        return warningMangerStationService.getInFoWarningThreshold(idThreshold);
    }

    @PostMapping
    DefaultResponseDTO createWarningManagerStation(@RequestBody @Valid WarningManagerStationDTO warningManagerStationDTO) throws SQLException{
        return warningMangerStationService.createWarningManagerStation(warningManagerStationDTO);
    }
    @GetMapping("/get-warning-manager-detail")
    public List<WarningMangerDetailInfoDTO> getWarningMangerDetailInfoDTOs(@RequestParam  Long warningManageStationId) throws SQLException{
        return warningMangerStationService.getWarningMangerDetailInfoDTOs(warningManageStationId);
    }

    @PutMapping
    public DefaultResponseDTO editWarningManagerStation( @RequestBody @Valid WarningManagerStationDTO warningManagerStationDTO) throws SQLException{
        return warningMangerStationService.editWarningManagerStation(warningManagerStationDTO);
    }
    @DeleteMapping
    public DefaultResponseDTO deleteWarningManagerStation(@RequestBody List<Long> ids) throws SQLException{
        return warningMangerStationService.deleteWarningManagerStation(ids);
    }

    @PostMapping("/warning-manager-select")
    public List<ComboBoxStr> getStationComboBox(@RequestBody @Valid SelectWarningManagerStrVM selectVM) throws SQLException {
        return warningMangerStationService.getWarningComboBox(selectVM);
    }
}
