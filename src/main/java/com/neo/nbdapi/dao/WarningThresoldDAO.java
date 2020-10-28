package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ComboBox;

import java.sql.SQLException;
import java.util.List;

public interface WarningThresoldDAO {
    List<ComboBox> getListCodeWarningThreSold(String Query) throws SQLException;
}
