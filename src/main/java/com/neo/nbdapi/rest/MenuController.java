package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMenuVM;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.EditMenuVM;
import com.neo.nbdapi.services.MenuService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_MENU_MANAGE)
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * api get list menu pagination
     * @param defaultRequestPagingVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @PostMapping("/get-list-menu-pagination")
    public DefaultPaginationDTO getListMenuPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return menuService.getListMenuPagination(defaultRequestPagingVM);
    }

    /**
     * api get all menu
     * @return
     * @throws SQLException
     */
    @GetMapping("/get-all-menu")
    public List<Menu> getAllMenu() throws SQLException {
        return menuService.getAllMenu();
    }

    /**
     * api create menu
     * @param createMenuVM
     * @return
     */
    @PostMapping("/create-menu")
    public DefaultResponseDTO createMenu(@RequestBody @Valid CreateMenuVM createMenuVM) throws SQLException, BusinessException {
        return menuService.createMenu(createMenuVM);
    }

    /**
     * api edit menu
     * @param editMenuVM
     * @return
     */
    @PutMapping("/edit-menu")
    public DefaultResponseDTO editMenu(@RequestBody @Valid EditMenuVM editMenuVM) throws SQLException, BusinessException {
        return menuService.editMenu(editMenuVM);
    }

    /**
     * api delete menu
     * @param deleteMenuVM
     * @return
     */
    @DeleteMapping("/delete-menu")
    public DefaultResponseDTO deleteMenu(@RequestBody @Valid DefaultDeleteVM deleteMenuVM) throws SQLException, BusinessException {
        return menuService.deleteMenu(deleteMenuVM);
    }
}
