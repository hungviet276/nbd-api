package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.entity.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

public interface UserInfoDAO {
	UserInfo findUserInfoByUsername(String username) throws SQLException;

	List<NameUserDTO> getNameUser(SelectDTO selectDTO) throws SQLException;

	List<NameUserDTO> getNameUserByGroupId(GroupDetail groupDetail) throws SQLException;
}
