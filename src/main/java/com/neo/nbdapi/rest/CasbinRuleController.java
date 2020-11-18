package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.CasbinRule;
import com.neo.nbdapi.services.CasbinRuleService;
import com.neo.nbdapi.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CASSBIN_RULE)
public class CasbinRuleController {
    private Logger logger = LogManager.getLogger(CasbinRuleController.class);

    @Autowired
    private CasbinRuleService casbinRuleService;

    @PostMapping("/get-casbin-by-user-id")
    public List<CasbinRule> getCasbinRuleByUser(@RequestParam("userId") String userId) throws SQLException {
        return casbinRuleService.getCasbinRuleByUser(userId);
    }

    @PostMapping("/add-casbin")
    public DefaultResponseDTO addCasbin(@RequestBody List<CasbinRule> casbinRules) throws SQLException {
        return casbinRuleService.add(casbinRules);
    }

    @PostMapping("/update-casbin")
    public DefaultResponseDTO updateCasbin(@RequestBody List<CasbinRule> casbinRules) throws SQLException {
        return casbinRuleService.update(casbinRules);
    }

    @PostMapping("/delete-casbin")
    public DefaultResponseDTO deleteCasbin(@RequestBody List<CasbinRule> casbinRules) throws SQLException {
        return casbinRuleService.delete(casbinRules);
    }
}
