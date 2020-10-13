package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.GroupMailReceiveDetailVM;


import java.sql.SQLException;
import java.util.List;

public interface GroupMailReceiveDetailDAO {
    DefaultResponseDTO createGroupReceiveMailDetail(GroupMailReceiveDetailVM GroupMailReceiveDetailVM) throws SQLException;

    DefaultResponseDTO editGroupReceiveMailDetail(List<String> userIdsDell, List<String> userIdsAdd, Long idGroup) throws SQLException;

    List<String> getAllUserById(Long idGroup) throws SQLException;

    DefaultResponseDTO deleteGroupReceiveMailDetail(Long id) throws SQLException;
}
