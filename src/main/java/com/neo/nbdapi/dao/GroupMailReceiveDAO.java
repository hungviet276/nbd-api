package com.neo.nbdapi.dao;

import com.neo.nbdapi.entity.GroupMailReceive;

import java.sql.SQLException;

public interface GroupMailReceiveDAO {
    GroupMailReceive findGroupMailReceiveConfigById(Long id) throws SQLException;


    void editGroupMailReceive(GroupMailReceive groupMailReceive) throws SQLException;

    long countGroupMailReceiveById(long id) throws SQLException;

    void delete(long id) throws SQLException;
}
