package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.MailConfig;

import java.sql.SQLException;

public interface MailConfigDAO {
    MailConfig findMailConfigById(Long id) throws SQLException;

    void createMailConfig(MailConfig mailConfig) throws SQLException;

    void editMailConfig(MailConfig mailConfig) throws SQLException;

    long countMailConfigById(long id) throws SQLException;

    void delete(long id) throws SQLException;
}
