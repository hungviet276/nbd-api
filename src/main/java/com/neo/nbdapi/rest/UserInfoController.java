package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.services.UserInfoService;
import com.neo.nbdapi.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_USER_INFO)
public class UserInfoController {
    private Logger logger = LogManager.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/get-user-and-menu")
    public UserAndMenuDTO getUserInfoAndListMenu() throws SQLException {
        return userInfoService.getUserInfoAndListMenu();
    }

    @GetMapping("/get-name-user")
    public List<NameUserDTO> getUserInfo(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userInfoService.getNameUser(selectGroupDTO);
    }

    @PostMapping("/get-name-user-by-group-id")
    public List<NameUserDTO> getUserInfoByGroup(@RequestBody GroupDetail groupDetail) throws SQLException {
        return userInfoService.getNameUserByGroupId(groupDetail);
    }
}
