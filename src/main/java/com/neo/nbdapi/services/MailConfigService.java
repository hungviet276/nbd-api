package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

import java.sql.SQLException;

public interface MailConfigService {
	DefaultPaginationDTO getListMailConfigPagination(DefaultRequestPagingVM defaultRequestPagingVM)
			throws SQLException, BusinessException;

	DefaultResponseDTO createMailConfig(CreateMailConfigVM createMailConfigVM) throws SQLException;
}
