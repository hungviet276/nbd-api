package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.ApiUrlDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.entity.Menu;

import java.sql.SQLException;
import java.util.List;

public interface MenuDAO {
    List<Menu> findAll() throws SQLException;
    Menu findMenuById(Long menuId) throws SQLException;

    void createMenu(Menu menuCreate) throws SQLException;

    void editMenu(Menu menu) throws SQLException;

    Menu findPathOfMenuByMenuId(long menuIdFirst) throws SQLException;

    void deleteMenu(Menu menu) throws SQLException;

    List<MenuDTO> getListMenuAccessOfUserByUsername(String username) throws SQLException;

    List<ApiUrlDTO> getListApiUrAccessOfUserByUsername(String username) throws SQLException;
}
