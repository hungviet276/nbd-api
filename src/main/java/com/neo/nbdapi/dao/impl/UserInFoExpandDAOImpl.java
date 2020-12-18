package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.*;
import com.neo.nbdapi.exception.BusinessException;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserInFoExpandDAOImpl implements UserInFoExpandDAO {

    private Logger logger = LogManager.getLogger(UserInfoDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException {
        List<NameUserDTO> nameUserDTOs = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            NameUserDTO nameUserDTO = null;
            String sql = "select  u.id, u.name, u.email from user_info_expand u  where 1 = 1 ";

            if(selectGroupDTO.getTerm() != null){
                sql += "  and (u.name like ? or u.email like ?) ";
            } else if(selectGroupDTO.getIdGroup()!= null){
                sql +=" and u.id not in (select gd.user_info_expant from group_receive_mail_detail gd where gd.id_group_receive_mail = ?)";
            }
            sql+= " and rownum < 100";
            logger.debug("JDBC execute query UserInFoExpandDAOImpl : {}", sql);
            PreparedStatement statement = connection.prepareStatement(sql);

            if(selectGroupDTO.getTerm() != null && selectGroupDTO.getIdGroup()== null){
                statement.setString(1, "%"+ selectGroupDTO.getTerm()+"%");
                statement.setString(2, "%"+ selectGroupDTO.getTerm()+"%");
            } else if(selectGroupDTO.getTerm() != null && selectGroupDTO.getIdGroup()!= null){
                statement.setString(1, "%"+ selectGroupDTO.getTerm()+"%");
                statement.setString(2, "%"+ selectGroupDTO.getTerm()+"%");
                statement.setLong(3, selectGroupDTO.getIdGroup());
            } else if (selectGroupDTO.getTerm() == null && selectGroupDTO.getIdGroup()!= null) {
                statement.setLong(1, selectGroupDTO.getIdGroup());
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nameUserDTO = NameUserDTO
                        .builder()
                        .id(resultSet.getString("id"))
                        .name(resultSet.getString("name"))
                        .email(resultSet.getString("email"))
                        .build();
                nameUserDTOs.add(nameUserDTO);
            }
            if(statement != null){
                statement.close();
            }
        }
        return nameUserDTOs;
    }

    @Override
    public DefaultResponseDTO createUserExpand(UserExpandDTO userExpandDTO) throws SQLException, BusinessException {
        String sql = "insert into user_info_expand (id, name, mobile, code, email, gender, status, card_number, position) values (USER_EXPAND_SEQ.nextval, ?, ?,?,?, ?,?,?,?)";
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userExpandDTO.getNameOutSite());
            statement.setString(2, userExpandDTO.getPhoneOutSite());
            statement.setString(3, userExpandDTO.getCodeUserOutSite());
            statement.setString(4, userExpandDTO.getEmailOutSite());
            statement.setString(5, userExpandDTO.getSexOutSite());
            statement.setInt(6, userExpandDTO.getStatusOutSite());
            statement.setString(7, userExpandDTO.getIdOutSite());
            statement.setString(8, userExpandDTO.getPositionOutSite());
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLIntegrityConstraintViolationException e){
            return DefaultResponseDTO.builder().status(0).message("Mã người dùng đã tồn tại").build();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public DefaultResponseDTO editUser(UserExpandDTO userExpandDTO) throws SQLException, BusinessException {
        String sql = "update user_info_expand set name = ?, mobile = ?, code = ?, email = ?, gender = ?, status = ? , card_number =  ?, position = ? where id = ?";
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userExpandDTO.getNameOutSite());
            statement.setString(2, userExpandDTO.getPhoneOutSite());
            statement.setString(3, userExpandDTO.getCodeUserOutSite());
            statement.setString(4, userExpandDTO.getEmailOutSite());
            statement.setString(5, userExpandDTO.getSexOutSite());
            statement.setInt(6, userExpandDTO.getStatusOutSite());
            statement.setString(7, userExpandDTO.getIdOutSite());
            statement.setString(8, userExpandDTO.getPositionOutSite());
            statement.setLong(9, userExpandDTO.getId());
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLIntegrityConstraintViolationException e){
            return DefaultResponseDTO.builder().status(0).message("Mã người dùng đã tồn tại").build();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }

    @Override
    public DefaultResponseDTO delete(Long id) throws SQLException, BusinessException {
        String sql = "delete user_info_expand where id = ?";
        try (Connection connection = ds.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.executeUpdate();
            statement.close();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }


}
