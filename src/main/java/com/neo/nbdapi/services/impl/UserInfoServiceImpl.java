package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDAO userInfoDAO;

    @Override
    public UserAndMenuDTO getUserInfoAndListMenu() throws SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoDAO.findMenuAndApiUrlOfUser(username);
    }
}
