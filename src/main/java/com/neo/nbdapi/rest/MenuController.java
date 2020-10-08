package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.MenuService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_MENU_MANAGE)
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/get-list-menu-pagination")
    public DefaultPaginationDTO getListMenuPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return menuService.getListMenuPagination(defaultRequestPagingVM);
    }

    @GetMapping("/get-all-menu")
    public List<Menu> getAllMenu() throws SQLException {
        return menuService.getAllMenu();
    }
}
