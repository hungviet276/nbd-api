package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserInFoExpandService {
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;
}