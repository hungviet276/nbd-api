package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
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
public class StationDAOImpl implements StationDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public List<ComboBox> getStationComboBox(String query) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "select station_id as id, station_code as code,station_name as name from stations where 1=1";
            if(query!=null && !query.equals("")){
                sql = sql+ " and station_name like ?";
            }
            sql = sql + " and rownum < 100 and ISDEL = 0 and IS_ACTIVE = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(query!=null && !query.equals("")){
                statement.setString(1,"%"+query+"%");
            }
            ResultSet resultSet = statement.executeQuery();
            List<ComboBox> comboBoxes = new ArrayList<>();
            while (resultSet.next()) {
                ComboBox comboBox = ComboBox.builder().id(resultSet.getLong(1)).text(resultSet.getString(2)+"-"+resultSet.getString(3)).build();
                comboBoxes.add(comboBox);
            }
            statement.close();
            return comboBoxes;
        }
    }
}
