package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MailGroupConfigDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class MailGroupConfigDAOImpl implements MailGroupConfigDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException {
        String sqlInsertGroupReceiveMail ="insert into group_receive_mail (ID, NAME, DESCRIPTION, CREATED_AT, CREATED_BY, CODE, STATUS) values (GROUP_RECEIVE_MAIL_SEQ.nextval,?,?,sysdate,?,?,?)";
        String sqlInsertGroupReceiveMailDetail = "insert into GROUP_RECEIVE_MAIL_DETAIL (ID, ID_GROUP_RECEIVE_MAIL, USER_INFO_ID, USER_INFO_EXPANT) values (GROUP_RECEIVE_MAIL_DETAIL_SEQ.nextval, GROUP_RECEIVE_MAIL_SEQ.currval,?,?)";
        String sqlInsertWarningRecipents = "insert into warning_recipents (id, group_receove_mail_id, manage_warning_stations) values (WARNING_RECIPENTS_SEQ.nextval,GROUP_RECEIVE_MAIL_SEQ.currval,?)";

        Connection connection = ds.getConnection();
        PreparedStatement stmGroupReceiveMail = null;
        PreparedStatement stmGroupReceiveMailDetail = null;
        PreparedStatement stmWarningRecipents = null;
        try{
            connection.setAutoCommit(false);
            stmGroupReceiveMail = connection.prepareStatement(sqlInsertGroupReceiveMail);
            stmGroupReceiveMailDetail = connection.prepareStatement(sqlInsertGroupReceiveMailDetail);
            stmWarningRecipents = connection.prepareStatement(sqlInsertWarningRecipents);

            stmGroupReceiveMail.setString(1, mailGroupConFigVM.getName());
            stmGroupReceiveMail.setString(2,mailGroupConFigVM.getDescription());
            stmGroupReceiveMail.setString(3, mailGroupConFigVM.getUser());
            stmGroupReceiveMail.setString(4, mailGroupConFigVM.getCode());
            stmGroupReceiveMail.setInt(5, mailGroupConFigVM.getStatus());
            stmGroupReceiveMail.executeUpdate();

            for(String val : mailGroupConFigVM.getUserInSites()){
                stmGroupReceiveMailDetail.setString(1, val);
                stmGroupReceiveMailDetail.setNull(2, Types.BIGINT);
                stmGroupReceiveMailDetail.addBatch();
            }
            stmGroupReceiveMailDetail.executeBatch();

            stmGroupReceiveMailDetail.clearParameters();


            for(String val : mailGroupConFigVM.getUserOutSite()){
                stmGroupReceiveMailDetail.setNull(1, Types.BIGINT);
                stmGroupReceiveMailDetail.setString(2, val);
                stmGroupReceiveMailDetail.addBatch();
            }
            stmGroupReceiveMailDetail.executeBatch();

            for(String val : mailGroupConFigVM.getWarningConfig()){
                stmWarningRecipents.setString(1, val);
                stmWarningRecipents.addBatch();
            }
            stmWarningRecipents.executeBatch();
            connection.commit();
        } catch ( Exception e){
            throw  e;
        } finally {
             if(stmGroupReceiveMail != null){
                 stmGroupReceiveMail.close();
             }
             if(stmGroupReceiveMailDetail != null){
                 stmGroupReceiveMailDetail.close();
             }
             if(stmWarningRecipents!=null){
                 stmWarningRecipents.close();
             }
             if(connection!=null){
                 connection.close();
             }
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }
}
