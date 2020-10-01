package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.entity.UserInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserInfoDAOImpl implements UserInfoDAO {

    private Logger logger = LogManager.getLogger(UserInfoDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    /**
     * @param username
     * @return
     * @throws SQLException
     */
    @Override
    public UserInfo findUserInfoByUsername(String username) throws SQLException {
        UserInfo userInfo = null;
        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT id, password FROM user_info WHERE id = ?";
            // log sql
            logger.debug("JDBC execute query : {}", sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userInfo = UserInfo
                        .builder()
                        .id(resultSet.getString(1))
                        .password(resultSet.getString(2))
                        .build();
            }
            if (userInfo == null)
                throw new UsernameNotFoundException("Tài khoản không tồn tại trong hệ thống");
            return userInfo;
        }
    }
}
