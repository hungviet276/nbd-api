package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.UserAndMenuDTO;

import java.sql.SQLException;

public interface UserInfoService {
    UserAndMenuDTO getUserInfoAndListMenu() throws SQLException;
}
