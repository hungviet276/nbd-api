package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveDetailVM;

import java.sql.SQLException;
import java.util.List;

public interface GroupMailReceiveServiceDetail {
    DefaultPaginationDTO getGroupReceiveMailDetailPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    DefaultResponseDTO createGroupReceiveMailDetail(GroupMailReceiveDetailVM GroupMailReceiveDetailVM) throws SQLException;

    DefaultResponseDTO editGroupReceiveMailDetail(GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException;

    DefaultResponseDTO deleteGroupReceiveMailDetail(GroupDetail groupDetail) throws SQLException;
}
