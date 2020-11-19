package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.CasbinRule;

import java.sql.SQLException;
import java.util.List;

public interface CasbinRuleService {
    List<CasbinRule> getCasbinRuleByUser(String userId) throws SQLException;

    DefaultResponseDTO add(List<CasbinRule> casbinRules) throws SQLException;

    DefaultResponseDTO update(List<List<CasbinRule>> casbinRules) throws SQLException;

    DefaultResponseDTO delete(List<CasbinRule> casbinRule) throws SQLException;
}
