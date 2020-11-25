package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.EmailBuilder;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.entity.WarningManagerStation;

import java.sql.SQLException;
import java.util.List;

public interface MailConfigDAO {
    MailConfig findMailConfigById(Long id) throws SQLException;

    void createMailConfig(MailConfig mailConfig) throws SQLException;

    void editMailConfig(MailConfig mailConfig) throws SQLException;

    long countMailConfigById(long id) throws SQLException;

    void delete(long id) throws SQLException;

    EmailBuilder getEmailConfig();

    WarningManagerStation getMailContent(Long id);

    List<String> getGroupRieveMail(List<Long> idList);
}
