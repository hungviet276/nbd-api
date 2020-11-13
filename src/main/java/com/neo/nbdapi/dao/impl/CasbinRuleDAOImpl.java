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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CasbinRuleDAOImpl implements CasbinRuleDAO {
    @Autowired
    private HikariDataSource ds;

    private Logger logger = LogManager.getLogger(CasbinRuleDAOImpl.class);

    @Override
    public List<CasbinRule> getCasbinRuleByUser(String userId) throws SQLException {
        List<CasbinRule> casbinRules = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select  a.API_URL , a.METHOD, b.v0\n" +
                    "    from (select distinct ap.API_URL, ap.METHOD from  api ap) a \n" +
                    "    ,(select distinct cas.v0,cas.v1,cas.v2 from  casbin_rule cas) b \n" +
                    "        where a.API_URL = b.V1(+) and (b.V0 = ? or b.v0 is null)";
            logger.info("getCasbinRuleByUser sql : " + sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            CasbinRule casbinRule = null;
            while (resultSet.next()) {
                casbinRule = CasbinRule
                        .builder()
                        .v0(resultSet.getString("V0"))
                        .v1(resultSet.getString("API_URL"))
                        .v2(resultSet.getString("METHOD"))
                        .build();
                casbinRules.add(casbinRule);
            }
        } catch (Exception e) {
            logger.error("getCasbinRuleByUser : " + e.getMessage());
        }
        return casbinRules;
    }

    @Override
    public int addCasbin(List<CasbinRule> casbinRules) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            String sqlAddCasbin = "insert into casbin_rule(ptype, v0, v1, v2) values (?,?,?,?)";
            logger.info("c.toString() : " + toString());
            statement = connection.prepareStatement(sqlAddCasbin.toString());
            CasbinRule casbinRule = new CasbinRule();
            for (int i = 0; i < casbinRules.size(); i++) {
                casbinRule = casbinRules.get(i);
                statement.setString(1, "p");
                statement.setString(2, casbinRule.getV1());
                statement.setString(3, casbinRule.getV2());
                statement.setString(4, casbinRule.getV3());
                statement.addBatch();
            }
            result = statement.executeBatch().length;
            connection.commit();
            logger.info("addCasbin result addCasbin : " + result);
        } catch (Exception e) {
            logger.info("addCasbin : true");
            e.printStackTrace();
        } finally {
            closeConnection(connection, statement);
        }
        return result;
    }

    @Override
    public int update(CasbinRule casbinRuleOld, CasbinRule casbinRuleNew) throws SQLException {
        Connection connection = null;
        PreparedStatement createSpatial = null;
        int resultUpdate = 0;
        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);

            String updateSql = "UPDATE casbin_rule set v1 = :1, v2 = :2 where v0 = :3 and v1 = :4";
            createSpatial = connection.prepareStatement(updateSql);
            logger.debug("update casbinRule : v0 : " + casbinRuleOld.getV0() + " v1 : " + casbinRuleOld.getV1());
            createSpatial.setString(1, casbinRuleNew.getV1());
            createSpatial.setString(2, casbinRuleNew.getV2());
            createSpatial.setString(3, casbinRuleOld.getV0());
            createSpatial.setString(4, casbinRuleOld.getV1());
            resultUpdate = createSpatial.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, createSpatial);
        }
        return resultUpdate;
    }

    @Override
    public int delete(List<CasbinRule> casbinRules) throws SQLException {
        Connection connection = null;
        PreparedStatement createSpatial = null;
        int resultUpdate = 0;
        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            StringBuilder deleteSql = new StringBuilder("DELETE FROM casbin_rule WHERE v1 in ( ");
            CasbinRule casbinRule = new CasbinRule();
            for (int i = 0; i < casbinRules.size(); i++) {
                casbinRule = casbinRules.get(i);
                if (i == casbinRules.size() - 1) {
                    deleteSql.append("\'").append(casbinRule.getV1()).append("\'");
                } else {
                    deleteSql.append("\'").append(casbinRule.getV1()).append("\',");
                }
            }
            deleteSql.append(")");
            logger.info("deleteSql : ", deleteSql.toString());
            createSpatial = connection.prepareStatement(deleteSql.toString());
            resultUpdate = createSpatial.executeUpdate();
            if (resultUpdate > 0) {
                logger.info("delete casbinRule : v0 : " + resultUpdate);
                connection.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, createSpatial);
        }
        return resultUpdate;
    }

    private void closeConnection(Connection connection, PreparedStatement spatial) {
        try {
            if (connection != null)
                connection.close();
            if (spatial != null) {
                spatial.close();
            }
        } catch (Exception e) {
            logger.info("closeConnection : ", e.getMessage());
        }
    }
}
