package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.*;

import java.sql.SQLException;

public interface GroupMailRecevelService {

    DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    DefaultResponseDTO createGroupReceiveMails(CreateGroupMailReceiveVM createGroupMailReceiveVM) throws SQLException;

    DefaultResponseDTO editGroupReceiveMail(EditMailConfigVM editMailConfigVM) throws SQLException, BusinessException;

    DefaultResponseDTO deleteGroupReceiveMail(DeleteMailConfigVM deleteMailConfigVM) throws SQLException, BusinessException;
}
