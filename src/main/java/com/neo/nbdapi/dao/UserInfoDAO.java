package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

public interface UserInfoDAO {
    UserInfo findUserInfoByUsername(String username) throws SQLException;
}
