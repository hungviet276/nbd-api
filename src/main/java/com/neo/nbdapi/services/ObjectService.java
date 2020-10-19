package com.neo.nbdapi.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.CallableStatement;

import org.apache.log4j.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
// @CacheConfig(cacheNames={"users"}) // tells Spring where to store
public class ObjectService {
    private HikariDataSource dataSource;

    @Autowired
    public void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public synchronized List<Map<Object, Object>> qry(Map<Object, Object> params, String sql, String... order) {
        List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
        Connection conn = null;
        CallableStatement ps = null;
        Map<Object, Object> item = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareCall(sql);
            int i = 1;
            for (String s : order) {
                ps.setObject(i, params.get(s));
                i++;
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                item = new HashMap<>();
                for (i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    item.put(rs.getMetaData().getColumnLabel(i + 1),
                            rs.getObject(rs.getMetaData().getColumnLabel(i + 1)));
                }
                list.add(item);
            }
            rs.close();
            return list;
        } catch (SQLException e) {
            Logger.getLogger("ws-error")
                    .error(params.toString() + "|SQL: ========>" + sql + "| Exception: ===========>" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public synchronized List<Map<Object, Object>> ref(Map<Object, Object> params, String sql, String... order) {
        // simulateSlowService();
        List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
        Connection conn = null;
        CallableStatement ps = null;
        Map<Object, Object> item = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareCall(sql);
            int i = 1;
            for (String s : order) {
                ps.setObject(i, params.get(s));
                i++;
            }
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                item = new HashMap<>();
                for (i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    item.put(rs.getMetaData().getColumnLabel(i + 1),
                            rs.getObject(rs.getMetaData().getColumnLabel(i + 1)));
                }
                list.add(item);
            }
            rs.close();
            return list;
        } catch (SQLException e) {
            Logger.getLogger("ws-error")
                    .error(params.toString() + "|SQL: ========>" + sql + "| Exception: ===========>" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public synchronized Object rej(Map<Object, Object> params, String sql, String... order) {
        // simulateSlowService();
        Object result = null;
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareCall(sql);
            int i = 1;
            for (String s : order) {
                ps.setObject(i, params.get(s));
                i++;
            }
            ps.execute();
            rs = ps.getResultSet();
            rs.next();
            result = rs.getObject(1);
            rs.close();
            return result;
        } catch (SQLException e) {
            Logger.getLogger("ws-error")
                    .error(params.toString() + "|SQL: ========>" + sql + "| Exception: ===========>" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    // private void simulateSlowService() {
    // try {
    // Thread.sleep(3000L);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }

    public synchronized Object val(Map<Object, Object> params, String sql, String... order) {
        Object result = null;
        Connection conn = null;
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareCall(sql);
            int i = 1;
            for (String s : order) {
                ps.setObject(i, params.get(s));
                i++;
            }
            ps.execute();
            rs = ps.getResultSet();
            rs.next();
            result = rs.getObject(1);
            rs.close();
            return result;
        } catch (SQLException e) {
            Logger.getLogger("ws-error")
                    .error(params.toString() + "|SQL: ========>" + sql + "| Exception: ===========>" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public synchronized Object update(Map<Object, Object> params, String sql, String... order) {
        Object result = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            int i = 1;
            for (String s : order) {
                ps.setObject(i, params.get(s));
                i++;
            }
            result = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            Logger.getLogger("ws-error")
                    .error(params.toString() + "|SQL: ========>" + sql + "| Exception: ===========>" + e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
        }
        return result;
    }


}
