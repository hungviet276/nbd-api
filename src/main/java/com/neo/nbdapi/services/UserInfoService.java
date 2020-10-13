package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;

import java.sql.SQLException;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;

public interface UserInfoService {
	UserAndMenuDTO getUserInfoAndListMenu() throws SQLException;

	UserAndMenuDTO getUserInfoAndListMenu(String username, String password) throws SQLException;

	List<NameUserDTO> getNameUser(SelectDTO selectDTO) throws SQLException;

	List<NameUserDTO> getNameUserByGroupId(GroupDetail groupDetail) throws SQLException;

}
