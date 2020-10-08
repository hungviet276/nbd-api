package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.Menu;

import java.sql.SQLException;
import java.util.List;

public interface MenuDAO {
    List<Menu> findAll() throws SQLException;
}
