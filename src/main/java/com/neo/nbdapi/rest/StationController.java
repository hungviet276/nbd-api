package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.utils.Constants;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_STATION)
public class StationController {
    @PostMapping("/station-select")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody SelectVM selectVM) throws SQLException, BusinessException {
        return null;
    }
}
