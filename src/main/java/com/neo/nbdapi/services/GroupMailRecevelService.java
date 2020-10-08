package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveVM;

import java.sql.SQLException;

public interface GroupMailRecevelService {

    DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    DefaultResponseDTO createGroupReceiveMails(GroupMailReceiveVM groupMailReceiveVM) throws SQLException;
}
