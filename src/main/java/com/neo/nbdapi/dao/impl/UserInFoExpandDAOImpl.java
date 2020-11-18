package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
