package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDAO userInfoDAO;

    @Override
    public UserAndMenuDTO getUserInfoAndListMenu() {
        return null;
    }
}
