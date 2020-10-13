package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.*;

import java.sql.SQLException;

public interface GroupMailReceiveService {

    DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    DefaultResponseDTO createGroupReceiveMails(GroupMailReceiveVM groupMailReceiveVM) throws SQLException;

    DefaultResponseDTO editGroupReceiveMail(GroupMailReceiveVM groupMailReceiveVM) throws SQLException, BusinessException;

    DefaultResponseDTO deleteGroupReceiveMail(DefaultDeleteVM defaultDeleteVM) throws SQLException, BusinessException;
}
