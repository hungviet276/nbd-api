package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.MenuDAO;
import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.ApiUrlDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private MenuDAO menuDAO;

	@Override
	public UserAndMenuDTO getUserInfoAndListMenu() throws SQLException {
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		String username = user.getName();
		List<MenuDTO> menuList = menuDAO.getListMenuAccessOfUserByUsername(username);
		List<ApiUrlDTO> apiUrlDTOList = menuDAO.getListApiUrAccessOfUserByUsername(username);
		return UserAndMenuDTO
				.builder()
				.menus(menuList)
				.urlApi(apiUrlDTOList)
				.build();
	}

}
