package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

import java.sql.SQLException;

public interface ConfigValueTypeService {
    DefaultPaginationDTO getListConfigValueType(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
}
