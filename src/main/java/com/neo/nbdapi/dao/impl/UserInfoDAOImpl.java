package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.entity.UserInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserInfoDAOImpl implements UserInfoDAO {

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInfo findUserInfoByUsername(String username) throws SQLException {
        UserInfo userInfo = null;
        System.out.println(passwordEncoder.encode("administrator"));
        try {
            Connection connection = ds.getConnection();
            String sql = "SELECT id, password FROM user_info WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                userInfo = UserInfo
                        .builder()
                        .id(resultSet.getString(1))
                        .password(resultSet.getString(2))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
}
