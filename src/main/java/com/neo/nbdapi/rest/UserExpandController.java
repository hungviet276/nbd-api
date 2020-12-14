package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.UserExpandService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_USER_EXPAND)
public class UserExpandController {
    @Autowired
    private UserExpandService userExpandService;

    @PostMapping("/get-name-user-out-site")
    public List<NameUserDTO> getUserInfo(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userExpandService.getNameUser(selectGroupDTO);
    }
    @PostMapping("/users-info-expand")
    public DefaultPaginationDTO getUserslist(@RequestBody DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return userExpandService.getListMailConfigPagination(defaultRequestPagingVM);
    }

    @PostMapping
    public DefaultResponseDTO createUserExpand(@RequestBody @Valid UserExpandDTO userExpandDTO) throws SQLException, BusinessException {
        return userExpandService.createUserExpand(userExpandDTO);
    }
    @PutMapping
    public DefaultResponseDTO editUserExpand(@RequestBody @Valid UserExpandDTO userExpandDTO) throws SQLException, BusinessException {
        return userExpandService.editUser(userExpandDTO);
    }

    @DeleteMapping
    public DefaultResponseDTO deleteUserExpand(@RequestParam Long id) throws SQLException, BusinessException {
        return userExpandService.delete(id);
    }

}
