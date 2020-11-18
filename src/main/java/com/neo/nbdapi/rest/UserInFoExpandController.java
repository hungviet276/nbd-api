package com.neo.nbdapi.rest;

import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.services.UserInFoExpandService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CONFIG_USER_EXPAND)
public class UserInFoExpandController {

    @Autowired
    private UserInFoExpandService userInFoExpandService;

    @PostMapping("/get-name-user-out-site")
    public List<NameUserDTO> getUserInfo(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userInFoExpandService.getNameUser(selectGroupDTO);
    }
}
