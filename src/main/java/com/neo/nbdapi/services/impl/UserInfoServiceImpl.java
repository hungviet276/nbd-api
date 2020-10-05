package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoDAO userInfoDAO;

	@Override
	public UserAndMenuDTO getUserInfoAndListMenu(String username,String password) throws SQLException {
		return userInfoDAO.findMenuAndApiUrlOfUser(username, password);
	}

	@Override
	public UserAndMenuDTO getUserInfoAndListMenu() throws SQLException {
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		String username = user.getName();
		return userInfoDAO.findMenuAndApiUrlOfUser(username);
	}

}
