package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import com.neo.nbdapi.services.UsersManagerService;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_USER_MANAGER)
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

    @PostMapping("/create_nv_temp")
    public String create_nv_temp(@RequestParam("act") String act, @RequestParam("menuId") String menuId, @RequestParam("threadId") String threadId, @RequestParam("type") String type) throws SQLException, BusinessException {
        return usersManagerService.create_nv_temp(act,menuId,threadId,type);
    }

    @PostMapping("/create_nq_temp")
    public String create_nq_temp(@RequestParam("nhomQuyen_id") String nhomquyen_id, @RequestParam("threadId") String threadId, @RequestParam("type") String type, @RequestParam("checkall") String checkAll) throws SQLException, BusinessException {
        return usersManagerService.create_nq_temp(nhomquyen_id,threadId,type,checkAll);
    }

    @GetMapping("/get_list_group_users")
    public List<ComboBox>  get_list_group_users() throws SQLException, BusinessException {
        return usersManagerService.get_list_group_users();
    }


    @PostMapping("/create_users")
    public String createUser(@RequestBody @Valid UsersManagerVM usersManagerVM) throws SQLException, BusinessException {
        return usersManagerService.createUser(usersManagerVM);
    }

    @PostMapping("/edit_users")
    public String editUsers(@RequestBody @Valid UsersManagerVM usersManagerVM) throws SQLException, BusinessException {
        return usersManagerService.editUsers(usersManagerVM);
    }

    @PostMapping("/delete_users")
    public String deleteUsers(@RequestParam("username") String username) throws SQLException, BusinessException {
        return usersManagerService.deleteUsers(username);
    }

    @PostMapping("/delete_temp")
    public String deleteTemp(@RequestParam("tempId") String tempId, @RequestParam("threadId") String threadId) throws SQLException, BusinessException {
        return usersManagerService.deleteTemp(tempId,threadId);
    }
}
