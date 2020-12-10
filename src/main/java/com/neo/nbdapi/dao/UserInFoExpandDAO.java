package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.exception.BusinessException;

import java.sql.SQLException;
import java.util.List;

public interface UserInFoExpandDAO {
    List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;
    DefaultResponseDTO createUserExpand(UserExpandDTO userExpandDTO) throws SQLException, BusinessException;
    DefaultResponseDTO editUser(UserExpandDTO userExpandDTO) throws SQLException, BusinessException;
    DefaultResponseDTO delete(Long id) throws SQLException, BusinessException;
}
