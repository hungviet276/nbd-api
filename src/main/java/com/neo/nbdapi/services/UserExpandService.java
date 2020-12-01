package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

public interface UserExpandService {
    DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException;
    List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;
}
