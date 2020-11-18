package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserInfoService {
	UserAndMenuDTO getUserInfoAndListMenu() throws SQLException;

	List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;

	List<NameUserDTO> getNameUserByGroupId(GroupDetail groupDetail) throws SQLException;

	List<NameUserDTO> getAllUserId() throws SQLException ;
}
