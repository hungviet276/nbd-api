package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.MailConfigService;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.services.impl.UsersManagerServiceImpl;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.*;
import java.util.*;
import java.util.Date;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_VALUE_TYPES)
public class UsersManagerController {

    private Logger logger = LogManager.getLogger(UsersManagerController.class);

    @Autowired
    private UsersManagerService usersManagerService;

    @Autowired
    private HikariDataSource ds;

    @PostMapping("/users_info_getlist")
    public DefaultPaginationDTO getUserslist(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return usersManagerService.getListUsersPagination(defaultRequestPagingVM);
    }

    @GetMapping("/get_header_tacvu")
    public List<Map<String, String>> getHeaderTacvu(@RequestParam("username") String userId) throws SQLException, BusinessException {
        return usersManagerService.getHeaderTacvu(userId);
    }

    @GetMapping("/get_menu_checked")
    public List<Map<String, String>> getMenu_checked(@RequestParam("nhomquyen_id") String id_nhomquyen,@RequestParam("username") String user_id,@RequestParam("thread_id") String thread_id) throws SQLException, BusinessException {
        return usersManagerService.getMenu_checked(id_nhomquyen,user_id,thread_id);
    }

    @GetMapping("/get_role")
    public List<Map<String, String>> get_role(@RequestParam("username") String user_id,@RequestParam("thread_id") String thread_id) throws SQLException, BusinessException {
        return usersManagerService.get_role(user_id,thread_id);
    }

    @GetMapping("/create_nv_temp")
    public List<Map<String, String>> create_nv_temp(@RequestParam("act") String act,@RequestParam("menuId") String menuId,@RequestParam("threadId") String threadId,@RequestParam("type") String type) throws SQLException, BusinessException {
        return usersManagerService.create_nv_temp(act,menuId,threadId,type);
    }

}
