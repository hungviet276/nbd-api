package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.CasbinRuleDAO;
import com.neo.nbdapi.entity.CasbinRule;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CasbinRuleDAOImpl implements CasbinRuleDAO {
    @Autowired
    private HikariDataSource ds;

    private Logger logger = LogManager.getLogger(CasbinRuleDAOImpl.class);

    @Override
    public List<CasbinRule> getCasbinRuleByUser(String userId) {
        List<CasbinRule> casbinRules = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select a.*, c.v0 from api a ,\n" +
                    "    (select distinct a.API_URL , a.METHOD, b.v0 from (select distinct ap.API_URL, ap.METHOD from  api ap) a \n" +
                    "    ,(select distinct cas.v0,cas.v1,cas.v2 from  casbin_rule cas) b  where a.API_URL = b.V1 and b.V0 =?) c \n" +
                    "where a.API_URL = c.API_URL(+)";
            logger.info("getCasbinRuleByUser sql : " + sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            CasbinRule casbinRule;
            while (resultSet.next()) {
                casbinRule = CasbinRule
                        .builder()
                        .v0(resultSet.getString("V0"))
                        .v1(resultSet.getString("API_URL"))
                        .v2(resultSet.getString("METHOD"))
                        .build();
                casbinRules.add(casbinRule);
            }
            logger.info("casbinRules.size() : " + casbinRules.size());
        } catch (Exception e) {
            logger.error("getCasbinRuleByUser : " + e.getMessage());
        }
        return casbinRules;
    }
}
