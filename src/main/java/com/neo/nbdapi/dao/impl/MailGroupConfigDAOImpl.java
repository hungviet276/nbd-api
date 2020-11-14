package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MailGroupConfigDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;

public class MailGroupConfigDAOImpl implements MailGroupConfigDAO {
    @Override
    public DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) {
        String sqlInSertGroupReceiveMail ="insert into group_receive_mail (ID, NAME, DESCRIPTION, CREATED_AT, CREATED_BY, CODE, STATUS) values (GROUP_RECEIVE_MAIL_SEQ.nextval,?,?,?,?,?,?)";
        // việc thứ 2 là insert user ngoài hệ thông
        // việc thứ 3 là insert vào bảnh con sinh ra
        // sau tất cả insert vào bảng config vậy là xong

        return null;
    }
}
