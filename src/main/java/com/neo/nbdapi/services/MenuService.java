package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMenuVM;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.EditMenuVM;

import java.sql.SQLException;
import java.util.List;

public interface MenuService {
    DefaultPaginationDTO getListMenuPagination(DefaultRequestPagingVM defaultRequestPagingVM);

    List<Menu> getAllMenu() throws SQLException;

    DefaultResponseDTO createMenu(CreateMenuVM createMenuVM) throws SQLException, BusinessException;

    DefaultResponseDTO editMenu(EditMenuVM editMenuVM) throws SQLException, BusinessException;

    DefaultResponseDTO deleteMenu(DefaultDeleteVM deleteMenuVM) throws SQLException, BusinessException;
}
