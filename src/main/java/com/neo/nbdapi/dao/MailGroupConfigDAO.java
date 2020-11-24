package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.UserExpandReceiveMail;
import com.neo.nbdapi.entity.UserInfoReceiveMail;
import com.neo.nbdapi.entity.WarningRecipentReceiveMail;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;

import java.sql.SQLException;
import java.util.List;

public interface MailGroupConfigDAO {
    DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException;
    List<Object> getInfoMailReceive(Long id) throws SQLException;
    DefaultResponseDTO editMailGroupConfig(MailGroupConFigVM mailGroupConFigVM,
                                           List<UserInfoReceiveMail> userInfoReceiveMailDeletes,
                                           List<UserInfoReceiveMail> userInfoReceiveMailInserts,
                                           List<UserExpandReceiveMail> userExpandReceiveMailInserts,
                                           List<UserExpandReceiveMail> userExpandReceiveMailDelete,
                                           List<WarningRecipentReceiveMail> warningRecipentReceiveMailDeletes,
                                           List<WarningRecipentReceiveMail> warningRecipentReceiveMailInsert) throws SQLException;

    DefaultResponseDTO deleteMailGroupConfig(Long id) throws SQLException;

}
