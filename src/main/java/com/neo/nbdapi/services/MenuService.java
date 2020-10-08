package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

import java.sql.SQLException;
import java.util.List;

public interface MenuService {
    DefaultPaginationDTO getListMenuPagination(DefaultRequestPagingVM defaultRequestPagingVM);

    List<Menu> getAllMenu() throws SQLException;
}
