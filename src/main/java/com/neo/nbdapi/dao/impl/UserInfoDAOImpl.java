package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.entity.UserInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserInfoDAOImpl implements UserInfoDAO {

    private Logger logger = LogManager.getLogger(UserInfoDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    /**
     * get user info by username
     * @param username
     * @return
     * @throws SQLException
     */
    @Override
    public UserInfo findUserInfoByUsername(String username) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            UserInfo userInfo = null;
            String sql = "SELECT id, password FROM user_info WHERE id = ?";
            // log sql
            logger.debug("JDBC execute query : {}", sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userInfo = UserInfo
                        .builder()
                        .id(resultSet.getString(1))
                        .password(resultSet.getString(2))
                        .build();
            }
            if (userInfo == null)
                throw new UsernameNotFoundException("Tài khoản không tồn tại trong hệ thống");
            return userInfo;
        }
    }

    /**
     * get list menu and api url of user
     * @param username
     * @return
     * @throws SQLException
     */
    @Override
    public UserAndMenuDTO findMenuAndApiUrlOfUser(String username, String ...args) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            logger.debug("Username: {}", username);
            // get list menu access of user
            String sqlGetListMenuAcc = "SELECT mn.id menu_id, mn.detail_file menu_url FROM user_info ui JOIN user_role ur ON ui.id = ur.user_id JOIN role r ON ur.role_id = r.id JOIN menu_access mnacc ON r.id = mnacc.role_id JOIN menu mn ON mn.id = mnacc.menu_id WHERE ui.id = ?";
            PreparedStatement statementGetListMenuAcc = connection.prepareStatement(sqlGetListMenuAcc);
            statementGetListMenuAcc.setString(1, username);
            ResultSet resultSetMenuAcc = statementGetListMenuAcc.executeQuery();
            List<MenuDTO> menuDTOList = new ArrayList<>();
            while (resultSetMenuAcc.next()) {
                MenuDTO menuItem = MenuDTO
                        .builder()
                        .id(resultSetMenuAcc.getInt(1))
                        .menuUrl(resultSetMenuAcc.getString(2))
                        .build();
                menuDTOList.add(menuItem);
            }

            // get list api url access of user
            String sqlGetListApiUrl = "SELECT v1, v2 FROM casbin_rule cr WHERE v0 = ?";
            PreparedStatement statementGetListApiUrl = connection.prepareStatement(sqlGetListApiUrl);
            statementGetListApiUrl.setString(1, username);
            ResultSet resultSetApiUrl = statementGetListApiUrl.executeQuery();
            List<ApiUrlDTO> apiUrlList = new ArrayList<>();
            while (resultSetApiUrl.next()) {
                ApiUrlDTO apiUrlDTO = ApiUrlDTO
                        .builder()
                        .url(resultSetApiUrl.getString(1))
                        .method(resultSetApiUrl.getString(2))
                        .build();
                apiUrlList.add(apiUrlDTO);
            }

            UserAndMenuDTO userAndMenuDTO = UserAndMenuDTO.builder()
                    .userId(username)
					.password(args[0])
                    .menus(menuDTOList)
                    .urlApi(apiUrlList)
                    .build();
            return userAndMenuDTO;
        }
    }

    @Override
    public List<NameUserDTO> getNameUser(SelectDTO selectDTO) throws SQLException {
        List<NameUserDTO> nameUserDTOs = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            NameUserDTO nameUserDTO = null;
            String sql = "";
            if(selectDTO.getTerm() == null){
                sql = "select u.id, u.name from user_info u where u.id not in(select gd.user_info_id from group_receive_mail_detail gd where gd.id_group_receive_mail = ?)";
            } else{
                sql = "select id, name from user_info where select u.id, u.name from user_info u where u.id not in(select gd.user_info_id from group_receive_mail_detail gd where gd.id_group_receive_mail = ?)and u.name like ? and rownum < 100";
            }
            // log sql
            logger.debug("JDBC execute query : {}", sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, selectDTO.getIdGroup());
            if(selectDTO.getTerm() != null){
                statement.setString(2, "%"+selectDTO.getTerm()+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nameUserDTO = NameUserDTO
                        .builder()
                        .id(resultSet.getString("id"))
                        .name(resultSet.getString("name"))
                        .build();
                nameUserDTOs.add(nameUserDTO);
            }
        }
        return nameUserDTOs;
    }

    @Override
    public List<NameUserDTO> getNameUserByGroupId(GroupDetail groupDetail) throws SQLException {
        List<NameUserDTO> nameUserDTOs = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            NameUserDTO nameUserDTO = null;
            String sql = "select u.id, u.name from group_receive_mail_detail gd, user_info u where gd.id_group_receive_mail = ? and gd.USER_INFO_ID = u.id and gd.id = ?";
            logger.debug("JDBC execute query : {}", sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, groupDetail.getIdGroup());
            statement.setLong(2, groupDetail.getIdDetail());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nameUserDTO = NameUserDTO
                        .builder()
                        .id(resultSet.getString("id"))
                        .name(resultSet.getString("name"))
                        .build();
                nameUserDTOs.add(nameUserDTO);
            }
        }
        return nameUserDTOs;
    }
}
