package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.services.UserInFoExpandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserInFoExpandServiceImpl implements UserInFoExpandService {

    @Autowired
    private UserInFoExpandDAO userInFoExpandDAO;

    @Override
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userInFoExpandDAO.getNameUser(selectGroupDTO);
    }
}
