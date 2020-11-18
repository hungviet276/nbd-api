package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.CasbinRuleDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.CasbinRule;
import com.neo.nbdapi.services.CasbinRuleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CasbinRuleServiceImpl implements CasbinRuleService {
    private Logger logger = LogManager.getLogger(CasbinRuleServiceImpl.class);

    @Autowired
    private CasbinRuleDAO casbinRuleDAO;

    @Autowired
    private Enforcer enforcer;

    @Override
    public List<CasbinRule> getCasbinRuleByUser(String userId) throws SQLException {
        return casbinRuleDAO.getCasbinRuleByUser(userId);
    }

    @Override
    public DefaultResponseDTO add(List<CasbinRule> casbinRules) {
        boolean isSuccess = false;
        try {
            for (CasbinRule casbinRule : casbinRules) {
                isSuccess = enforcer.addPolicy(casbinRule.getV0(), casbinRule.getV1(), casbinRule.getV2());
                if (isSuccess) enforcer.savePolicy();
                logger.info("add casbin : " + isSuccess + casbinRule.toString());
            }
            if (isSuccess) {
                return DefaultResponseDTO.builder().status(1).message("Thành công").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultResponseDTO.builder().status(-1).message("Thất bại").build();
    }

    @Override
    public DefaultResponseDTO update(List<List<CasbinRule>> casbinRules) {
        try {
            List<CasbinRule> deleteList = casbinRules.get(0);
            List<CasbinRule> addList = casbinRules.get(1);
            if (deleteList.size() > 0) delete(deleteList);
            if (addList.size() > 0) add(addList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public DefaultResponseDTO delete(List<CasbinRule> casbinRules) {
        boolean isSuccess;
        try {
            for (CasbinRule casbinRule : casbinRules) {
                isSuccess = enforcer.removeNamedPolicy("p", casbinRule.getV0(), casbinRule.getV1(), casbinRule.getV2());
                enforcer.savePolicy();
                logger.info("delete casbin : " + isSuccess + casbinRule.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }
}
