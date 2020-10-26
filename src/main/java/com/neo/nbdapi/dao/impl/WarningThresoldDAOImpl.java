package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.WarningThresoldDAO;
import com.neo.nbdapi.entity.ComboBox;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WarningThresoldDAOImpl implements WarningThresoldDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getListCodeWarningThreSold(String query) throws SQLException {
        List<ComboBox> comboBoxes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            String sql = "select id, code from warning_threshold where 1=1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and code like ?";
            }
            sql += " and ROWNUM < 100";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,query);
            }
            ResultSet resultSet = statement.executeQuery();
            ComboBox comboBox = null;
            while (resultSet.next()) {
                comboBox = ComboBox.builder().id(resultSet.getLong("id"))
                        .text(resultSet.getString("code")).build();
                comboBoxes.add(comboBox);
            }
            return comboBoxes;
        }
    }
}
