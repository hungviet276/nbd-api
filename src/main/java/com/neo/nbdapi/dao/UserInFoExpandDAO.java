package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserInFoExpandDAO {
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;
}
