package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.ManageCDHService;
import com.neo.nbdapi.services.ManageOutputService;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CDH_HISTORY)
public class CdhHistoryController {

    private Logger logger = LogManager.getLogger(CdhHistoryController.class);

    @Autowired
    private ManageCDHService manageCDHService;

    @Autowired
    private HikariDataSource ds;

    @PostMapping("/get_list_outputs")
    public DefaultPaginationDTO getListOutpust(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return manageCDHService.getListOutpust(defaultRequestPagingVM);
    }

    @GetMapping("/get_list_stations")
    public List<ComboBox>  get_list_group_users(@RequestParam("username") String userId) throws SQLException, BusinessException {
        return manageCDHService.getListStations(userId);
    }

    @GetMapping("/getList_parameter_byStationId")
    public List<ComboBox>  getListParameterByStations(@RequestParam("stationId") String stationId) throws SQLException, BusinessException {
        return manageCDHService.getListParameterByStations(stationId);
    }

}
