package com.neo.nbdapi.rest;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.services.UserInfoService;
import com.neo.nbdapi.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_USER_INFO)
public class UserInfoController {
	private Logger logger = LogManager.getLogger(UserInfoController.class);

	@Autowired
	private UserInfoService userInfoService;

	@GetMapping("/get-user-and-menu")
	public UserAndMenuDTO getUserInfoAndListMenu() throws SQLException {
		return userInfoService.getUserInfoAndListMenu();
	}
}
