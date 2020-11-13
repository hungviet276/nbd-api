package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.entity.UserInfo;

import java.sql.SQLException;
import java.util.List;

public interface UserInfoDAO {
	UserInfo findUserInfoByUsername(String username) throws SQLException;

	List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException;

	List<NameUserDTO> getNameUserByGroupId(GroupDetail groupDetail) throws SQLException;

	List<NameUserDTO> getAllUserId() throws SQLException;
}
