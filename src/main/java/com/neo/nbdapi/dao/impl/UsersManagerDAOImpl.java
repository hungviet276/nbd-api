package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MailConfigDAO;
import com.neo.nbdapi.dao.UsersManagerDAO;
import com.neo.nbdapi.entity.MailConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class UsersManagerDAOImpl implements UsersManagerDAO {

    @Autowired
    private HikariDataSource ds;


}
