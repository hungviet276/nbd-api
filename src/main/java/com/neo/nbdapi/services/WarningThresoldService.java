package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ComboBox;

import java.sql.SQLException;
import java.util.List;

public interface WarningThresoldService {
    List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException;
}
