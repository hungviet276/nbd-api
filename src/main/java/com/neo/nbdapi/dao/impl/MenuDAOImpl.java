package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MenuDAO;
import com.neo.nbdapi.entity.Menu;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDAOImpl implements MenuDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<Menu> findAll() throws SQLException {
        String sql = "SELECT id, name, display_order, picture_file, detail_file, menu_level, parent_id, publish, created_date, modified_date, created_user, modified_user, sys_id FROM menu WHERE 1 = 1 ";
        List<Menu> menuList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
        ){
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Menu menu = Menu.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .displayOrder(resultSet.getInt("display_order"))
                        .pictureFile(resultSet.getString("picture_file"))
                        .menuLevel(resultSet.getInt("menu_level"))
                        .parentId(resultSet.getInt("parent_id"))
                        .publish(resultSet.getInt("publish"))
                        .createdDate(resultSet.getDate("created_date"))
                        .modifiedDate(resultSet.getDate("modified_date"))
                        .createdUser(resultSet.getString("created_user"))
                        .modifiedUser(resultSet.getString("modified_user"))
                        .sysId(resultSet.getInt("sys_id"))
                        .build();
                menuList.add(menu);
            }
            return menuList;
        }
    }
}
