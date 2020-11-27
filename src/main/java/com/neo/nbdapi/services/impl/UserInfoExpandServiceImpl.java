package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.UserInfoExpandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserInfoExpandServiceImpl implements UserInfoExpandService {

    @Autowired
    private UserInFoExpandDAO userInFoExpandDAO;

    @Override
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userInFoExpandDAO.getNameUser(selectGroupDTO);
    }

    @Override
    public DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return null;
    }
}
