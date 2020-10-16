package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;

import java.sql.SQLException;
import java.util.List;

public interface ValueTypeDAO {
    public List<ComboBox> getValueTypesSelect(String query) throws SQLException;
}
