package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectVM;
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
    public List<ComboBox> getListParameterWarningConfig(@RequestBody SelectWarningManagerVM selectVM) throws SQLException{
        return warningMangerStationService.getListParameterWarningConfig(selectVM);
    }
    @PostMapping("/get-list-warning-config-threshold")
    public List<ComboBox> getListParameterWarningThreshold(@RequestBody SelectWarningManagerVM selectVM) throws SQLException{
        return warningMangerStationService.getListParameterWarningThreshold(selectVM);
    }
}
