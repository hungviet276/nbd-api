package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.UserAndMenuDTO;

import java.sql.SQLException;

import org.springframework.security.authentication.AuthenticationProvider;

public interface UserInfoService{
	UserAndMenuDTO getUserInfoAndListMenu() throws SQLException;
}
