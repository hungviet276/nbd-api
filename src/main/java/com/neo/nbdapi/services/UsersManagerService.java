package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UsersManagerService {
    DefaultPaginationDTO getListUsersPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;

    List<Map<String, String>> getHeaderTacvu(String userId) throws SQLException, BusinessException;

    List<Map<String, String>> getMenu_checked(String id_nhomquyen,String user_id,String thread_id) throws SQLException, BusinessException;

    List<Map<String, String>> get_role(String user_id,String thread_id) throws SQLException, BusinessException;

    List<Map<String, String>> create_nv_temp(String act,String menuId,String threadId,String type) throws SQLException, BusinessException;


}
