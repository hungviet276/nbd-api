package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.UserExpandService;
import com.neo.nbdapi.services.UserInfoExpandService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public DefaultPaginationDTO getUserslist(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return userExpandService.getListMailConfigPagination(defaultRequestPagingVM);
    }

}
