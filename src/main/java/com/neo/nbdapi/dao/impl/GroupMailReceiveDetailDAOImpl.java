package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.GroupMailReceiveDetailDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.GroupMailReceiveDetailVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupMailReceiveDetailDAOImpl implements GroupMailReceiveDetailDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public DefaultResponseDTO createGroupReceiveMailDetail(GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "insert into group_receive_mail_detail(id, ID_GROUP_RECEIVE_MAIL, USER_INFO_ID) values (GROUP_RECEIVE_MAIL_DETAIL_SEQ.nextval, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            List<String> userIds = groupMailReceiveDetailVM.getUserReceive();
            for(int i =0; i < userIds.size() ; i++){
                statement.setLong(1, groupMailReceiveDetailVM.getIdGroup());
                statement.setString(2, userIds.get(i));
                statement.addBatch();
            }
            statement.executeBatch();
            return DefaultResponseDTO.builder().status(1).message("thành công").build();
    }
}

    @Override
    public DefaultResponseDTO editGroupReceiveMailDetail(List<String> userIdsDell, List<String> userIdsAdd, Long idGroup) throws SQLException {

        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            String sqlDelete = "delete from group_receive_mail_detail where user_info_id = ? and id_group_receive_mail = ?";
            String sqlAdd = "insert into group_receive_mail_detail(id, ID_GROUP_RECEIVE_MAIL, USER_INFO_ID) values (GROUP_RECEIVE_MAIL_DETAIL_SEQ.nextval, ?, ?)";
            PreparedStatement statementDell = connection.prepareStatement(sqlDelete);
            PreparedStatement statementAdd = connection.prepareStatement(sqlAdd);

            for (int i = 0; i < userIdsDell.size(); i++) {
                statementDell.setString(1, userIdsDell.get(i));
                statementDell.setLong(2, idGroup);
                statementDell.addBatch();
            }
            statementDell.executeBatch();

            for (int i = 0; i < userIdsAdd.size(); i++) {
                statementAdd.setLong(1, idGroup);
                statementAdd.setString(2, userIdsAdd.get(i));
                statementAdd.addBatch();
            }
            statementAdd.executeBatch();
            connection.commit();
            statementDell.close();
            statementAdd.close();
            return DefaultResponseDTO.builder().status(1).message("thành công").build();
        }
    }

    @Override
    public List<String> getAllUserById(Long id) throws SQLException {
        List<String> idUser = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select user_info_id from group_receive_mail_detail where id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                idUser.add(resultSet.getString(1));
            }
            statement.close();
            return idUser;
        }
    }

    @Override
    public DefaultResponseDTO deleteGroupReceiveMailDetail(Long id) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "delete from group_receive_mail_detail where id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1,id);
            statement.executeUpdate();
            statement.close();
            return DefaultResponseDTO.builder().status(1).message("thành công").build();
        }
    }
}
