package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.CasbinRuleDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.CasbinRule;
import com.neo.nbdapi.services.CasbinRuleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CasbinRuleServiceImpl implements CasbinRuleService {
    private Logger logger = LogManager.getLogger(CasbinRuleServiceImpl.class);

    @Autowired
    private CasbinRuleDAO casbinRuleDAO;

    @Override
    public List<CasbinRule> getCasbinRuleByUser(String userId) throws SQLException {
        return casbinRuleDAO.getCasbinRuleByUser(userId);
    }

    @Override
    public DefaultResponseDTO add(List<CasbinRule> casbinRules) throws SQLException {
        DefaultResponseDTO responseDTO = new DefaultResponseDTO();
        try {
            int resultAdd = casbinRuleDAO.addCasbin(casbinRules);
            if (resultAdd > 0) {
                return DefaultResponseDTO.builder().status(1).message("Thành công").build();
            }
            return DefaultResponseDTO.builder().status(1).message("Thất bại").build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseDTO;
    }

    @Override
    public DefaultResponseDTO update(List<CasbinRule> casbinRules) throws SQLException {
        CasbinRule casbinRuleOld = casbinRules.get(0);
        CasbinRule casbinRuleNew = casbinRules.get(1);
        DefaultResponseDTO responseDTO = new DefaultResponseDTO();
        if (casbinRuleDAO.update(casbinRuleOld, casbinRuleNew) > 0) {
            return DefaultResponseDTO.builder().status(1).message("Thành công").build();
        }
        return DefaultResponseDTO.builder().status(1).message("Thất bại").build();
    }

    @Override
    public DefaultResponseDTO delete(List<CasbinRule> casbinRules) throws SQLException {
        DefaultResponseDTO responseDTO = new DefaultResponseDTO();
        int resultDelete = casbinRuleDAO.delete(casbinRules);
        if (resultDelete > 0) {
            return DefaultResponseDTO.builder().status(1).message("Thành công").build();
        }
        return DefaultResponseDTO.builder().status(1).message("Thất bại").build();
    }
}
