package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.CasbinRule;

import java.sql.SQLException;
import java.util.List;

public interface CasbinRuleDAO {
    List<CasbinRule> getCasbinRuleByUser(String userId) throws SQLException;

    int addCasbin(List<CasbinRule> casbinRule) throws SQLException;

    int update(CasbinRule casbinRuleOld, CasbinRule casbinRuleNew) throws SQLException;

    int delete(List<CasbinRule> casbinRules) throws SQLException;

}
