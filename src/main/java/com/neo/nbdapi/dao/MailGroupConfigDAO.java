package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;

import java.sql.SQLException;

public interface MailGroupConfigDAO {
    DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException;
}
