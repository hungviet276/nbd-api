package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MailConfigDAO;
import com.neo.nbdapi.entity.MailConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MailConfigDAOImpl implements MailConfigDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public MailConfig findMailConfigById(Long id) throws SQLException {
        String sql = "SELECT id, ip, port, username, password, domain, sender_name, email_address, protocol FROM mail_config WHERE id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            MailConfig mailConfig = null;
            while (resultSet.next()) {
                mailConfig = MailConfig.builder()
                        .id(resultSet.getLong("id"))
                        .ip(resultSet.getString("ip"))
                        .port(resultSet.getString("port"))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .domain(resultSet.getString("domain"))
                        .senderName(resultSet.getString("sender_name"))
                        .emailAddress(resultSet.getString("email_address"))
                        .protocol(resultSet.getString("protocol"))
                        .build();
            }
            return mailConfig;
        }
    }

    @Override
    public void createMailConfig(MailConfig mailConfig) throws SQLException {
        String sql = "INSERT INTO mail_config(id, ip, port, username, password, domain, sender_name, email_address, protocol) values (MAIL_CONFIG_SEQ.nextval, ?,?,?,?,?,?,?,?)";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            statement.setString(1, mailConfig.getIp());
            statement.setString(2, mailConfig.getPort());
            statement.setString(3, mailConfig.getUsername());
            statement.setString(4, mailConfig.getPassword());
            statement.setString(5, mailConfig.getDomain());
            statement.setString(6, mailConfig.getSenderName());
            statement.setString(7, mailConfig.getEmailAddress());
            statement.setString(8, mailConfig.getProtocol());
            statement.executeQuery();
        }
    }

    @Override
    public void editMailConfig(MailConfig mailConfig) throws SQLException {
        String sql = "UPDATE mail_config SET ip = ?, port = ?, username = ?, password = ?, domain = ?, sender_name = ?, email_address = ?, protocol = ? WHERE id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, mailConfig.getIp());
            statement.setString(2, mailConfig.getPort());
            statement.setString(3, mailConfig.getUsername());
            statement.setString(4, mailConfig.getPassword());
            statement.setString(5, mailConfig.getDomain());
            statement.setString(6, mailConfig.getSenderName());
            statement.setString(7, mailConfig.getEmailAddress());
            statement.setString(8, mailConfig.getProtocol());
            statement.setLong(9, mailConfig.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public long countMailConfigById(long id) throws SQLException {
        String sql = "SELECT COUNT(1) FROM mail_config WHERE id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM mail_config WHERE id = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
