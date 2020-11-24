package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MenuDAO;
import com.neo.nbdapi.dto.ApiUrlDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDAOImpl implements MenuDAO {

    private Logger logger = LogManager.getLogger(MenuDAOImpl.class);

    @Autowired
    private HikariDataSource ds;

    @Value(("${sysId}"))
    private int sysId;

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

    @Override
    public Menu findMenuById(Long menuId) throws SQLException {
        String sql = "SELECT id, name, display_order, picture_file, detail_file, menu_level, parent_id, publish, created_date, modified_date, created_user, modified_user, sys_id FROM menu WHERE id = ? ";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            Menu menu = null;
            statement.setLong(1, menuId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                menu = Menu.builder()
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
            }
            return menu;
        }
    }

    @Override
    public void createMenu(Menu menuCreate) throws SQLException {
        String sql = "INSERT INTO menu(id, name, display_order, picture_file, detail_file, menu_level, parent_id, publish, created_date, created_user) values (MENU_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, sysdate, ?)";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            statement.setString(1, menuCreate.getName());
            statement.setInt(2, menuCreate.getDisplayOrder());
            statement.setString(3, menuCreate.getPictureFile());
            statement.setString(4, menuCreate.getDetailFile());
            statement.setInt(5, menuCreate.getMenuLevel());
            statement.setLong(6, menuCreate.getParentId());
            statement.setInt(7, menuCreate.getPublish());
            statement.setString(8, SecurityContextHolder.getContext().getAuthentication().getName());
            statement.execute();
        }
    }

    @Override
    public void editMenu(Menu menu) throws SQLException {
        String sql = "UPDATE menu SET name = ?, display_order = ?, picture_file = ?, detail_file = ?, menu_level = ?, parent_id = ?, publish = ?, modified_date = sysdate, modified_user = ? WHERE id = ?";

        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            statement.setString(1, menu.getName());
            statement.setInt(2, menu.getDisplayOrder());
            statement.setString(3, menu.getPictureFile());
            statement.setString(4, menu.getDetailFile());
            statement.setInt(5, menu.getMenuLevel());
            statement.setLong(6, menu.getParentId());
            statement.setInt(7, menu.getPublish());
            statement.setString(8, SecurityContextHolder.getContext().getAuthentication().getName());
            statement.setLong(9, menu.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Menu findPathOfMenuByMenuId(long menuId) throws SQLException {
        String sql = "SELECT  mn.id, Sys_Connect_By_Path(mn.id,'/') as path FROM menu mn where mn.id = ? START WITH mn.parent_id = 0  CONNECT BY PRIOR mn.id = mn.parent_id";
        Menu menu = null;
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, menuId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                menu = Menu.builder()
                        .id(resultSet.getLong("id"))
                        .path(resultSet.getString("path"))
                        .build();
            }
            return menu;
        }
    }

    @Override
    public void deleteMenu(Menu menu) throws SQLException {
        String sql = " DELETE FROM menu mn2 where mn2.id in ( SELECT  mn.id FROM menu mn START WITH mn.id = ? CONNECT BY PRIOR mn.id = mn.parent_id )";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, menu.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<MenuDTO> getListMenuAccessOfUserByUsername(String username) throws SQLException {
        String sql = "SELECT mn.id menu_id, mn.name menu_name, mn.detail_file, mn.parent_id, mn.picture_file, mn.menu_level FROM menu mn JOIN user_menu_act uma ON mn.id = uma.menu_id  JOIN user_info ui ON ui.id = uma.user_id WHERE ui.id = ? AND uma.sys_id = ? AND act = ? ORDER BY mn.menu_level ASC, mn.display_order ASC, mn.id";
        List<MenuDTO> menuList = new ArrayList<>();
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, username);
            statement.setInt(2, sysId);
            statement.setString(3, Constants.MENU.ACTION_VIEW_MENU);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                MenuDTO menu = MenuDTO
                        .builder()
                        .id(resultSet.getLong("menu_id"))
                        .name(resultSet.getString("menu_name"))
                        .detailFile(resultSet.getString("detail_file"))
                        .parentId(resultSet.getLong("parent_id"))
                        .pictureFile(resultSet.getString("picture_file"))
                        .level(resultSet.getInt("menu_level"))
                        .build();
                menuList.add(menu);
            }
            return menuList;
        }
    }

    @Override
    public List<ApiUrlDTO> getListApiUrAccessOfUserByUsername(String username) throws SQLException {
        String sql = "SELECT v1, v2 FROM casbin_rule cr WHERE v0 = ?";
        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, username);
            ResultSet resultSetApiUrl = statement.executeQuery();
            List<ApiUrlDTO> apiUrlList = new ArrayList<>();
            while (resultSetApiUrl.next()) {
                ApiUrlDTO apiUrlDTO = ApiUrlDTO
                        .builder()
                        .url(resultSetApiUrl.getString(1))
                        .method(resultSetApiUrl.getString(2))
                        .build();
                apiUrlList.add(apiUrlDTO);
            }
            return apiUrlList;
        }
    }
}
