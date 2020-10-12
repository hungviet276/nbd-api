package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
public interface LogActService {
    DefaultPaginationDTO getListLogActPagination(DefaultRequestPagingVM defaultRequestPagingVM);

    List<MenuDTO> getListMenuViewLogOfUser() throws SQLException;
}
