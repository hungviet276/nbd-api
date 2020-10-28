package com.neo.nbdapi.rest.vm;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.services.ChangerPassService;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CHANGER_PASS)
public class ChangerPassController {

    private Logger logger = LogManager.getLogger(ChangerPassController.class);

    @Autowired
    private ChangerPassService changerPassService;

    @Autowired
    private HikariDataSource ds;

    @PostMapping("/changerPass")
    public String changerPass(@RequestBody @Valid ChangerPassVM changerPassVM) throws SQLException, BusinessException {
        return changerPassService.ChangerPass(changerPassVM);
    }

    @PostMapping("/getOldPassword")
    public String getOldPassword(@RequestParam("username") String user_id) throws SQLException, BusinessException {
        return changerPassService.getOldPass(user_id);
    }

}
