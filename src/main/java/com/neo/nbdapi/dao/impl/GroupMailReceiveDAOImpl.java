package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.GroupMailReceiveDAO;
import com.neo.nbdapi.entity.GroupMailReceive;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class GroupMailReceiveDAOImpl implements GroupMailReceiveDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public GroupMailReceive findGroupMailReceiveConfigById(Long id) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select id, name, description, code, status from group_receive_mail where id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            GroupMailReceive groupMailReceive = null;
            while (resultSet.next()) {
               groupMailReceive = GroupMailReceive.builder()
                       .id(resultSet.getLong("id"))
                       .name(resultSet.getString("name"))
                       .description(resultSet.getString("description"))
                       .code(resultSet.getString("code"))
                       .status(resultSet.getInt("status"))
                       .build();

            }
            return groupMailReceive;
        }
    }

    @Override
    public void editGroupMailReceive(GroupMailReceive groupMailReceive) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "update group_receive_mail set id = ?, name =?, description=?, code = ?, status = ? where id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, groupMailReceive.getId());
            statement.setString(2, groupMailReceive.getName());
            statement.setString(3, groupMailReceive.getDescription());
            statement.setString(4, groupMailReceive.getCode());
            statement.setInt(5, groupMailReceive.getStatus());
            statement.setLong(6, groupMailReceive.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public long countGroupMailReceiveById(long id) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "SELECT COUNT(1) FROM group_receive_mail WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "DELETE FROM group_receive_mail WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
