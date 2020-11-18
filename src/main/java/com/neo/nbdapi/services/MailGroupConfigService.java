package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;

import java.sql.SQLException;

public interface MailGroupConfigService {
    DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException;
}
