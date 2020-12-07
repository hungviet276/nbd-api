package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

import java.sql.SQLException;
import java.util.List;

public interface UserInfoExpandService {
     List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;
     DefaultPaginationDTO getListWarningThresholdStation(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
}
