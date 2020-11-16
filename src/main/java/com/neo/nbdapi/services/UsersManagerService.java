package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UsersManagerService {
    DefaultPaginationDTO getListUsersPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;


    List<Map<String, String>> getHeaderTacvu(String userId) throws SQLException, BusinessException;

    List<Map<String, String>> getMenu_checked(String id_nhomquyen,String user_id,String thread_id) throws SQLException, BusinessException;

    List<Map<String, String>> get_role(String user_id,String thread_id) throws SQLException, BusinessException;

    String create_nv_temp(String act,String menuId,String threadId,String type) throws SQLException, BusinessException;

    String create_nq_temp(String nhomquyen_id,String threadId,String type,String checkall) throws SQLException, BusinessException;

    List<ComboBox> get_list_group_users() throws SQLException, BusinessException;


    String createUser(UsersManagerVM usersManagerVM) throws SQLException, BusinessException;

    String editUsers(UsersManagerVM usersManagerVM) throws SQLException, BusinessException;

    String deleteTemp(String tempId,String threadId) throws SQLException, BusinessException;

    String deleteUsers(String username) throws SQLException, BusinessException;

     DefaultResponseDTO deleteUsersMutil(Long id) throws SQLException;

}
