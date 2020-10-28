package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.ChangerPassVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.UsersManagerVM;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ChangerPassService {
    String ChangerPass(ChangerPassVM changerPassVM) throws SQLException, BusinessException;

    String update_pass(String newspass) throws SQLException, BusinessException;

    String getOldPass(String userId) throws SQLException, BusinessException;

}
