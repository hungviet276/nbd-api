package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.MailGroupConfigDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.entity.UserExpandReceiveMail;
import com.neo.nbdapi.entity.UserInfo;
import com.neo.nbdapi.entity.UserInfoReceiveMail;
import com.neo.nbdapi.entity.WarningRecipentReceiveMail;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MailGroupConfigDAOImpl implements MailGroupConfigDAO {

    @Autowired
    private HikariDataSource ds;

    @Override
    public DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException {
        String sqlInsertGroupReceiveMail ="insert into group_receive_mail (ID, NAME, DESCRIPTION, CREATED_AT, CREATED_BY, CODE, STATUS) values (GROUP_RECEIVE_MAIL_SEQ.nextval,?,?,sysdate,?,?,?)";
        String sqlInsertGroupReceiveMailDetail = "insert into GROUP_RECEIVE_MAIL_DETAIL (ID, ID_GROUP_RECEIVE_MAIL, USER_INFO_ID, USER_INFO_EXPANT) values (GROUP_RECEIVE_MAIL_DETAIL_SEQ.nextval, GROUP_RECEIVE_MAIL_SEQ.currval,?,?)";
        String sqlInsertWarningRecipents = "insert into warning_recipents (id, GROUP_RECEIVE_MAIL_ID, manage_warning_stations) values (WARNING_RECIPENTS_SEQ.nextval,GROUP_RECEIVE_MAIL_SEQ.currval,?)";

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

    @Override
    public List<Object> getInfoMailReceive(Long id) throws SQLException {
        String sqlUserInSite = "select u.id, u.name, u.email from group_receive_mail_detail g inner join user_info u on u.id = g.user_info_id where g.id_group_receive_mail = ?";
        String sqlUserOutSite = "select ue.id, ue.name, ue.email from user_info_expand  ue inner join group_receive_mail_detail g on g.user_info_expant = ue.id where g.id_group_receive_mail = ?";
        String sqlWarning = "select w.id, s.station_id as station_id, s.station_name, ws.id as warning_id, ws.code   from warning_recipents w inner join warning_manage_stations ws on ws.id = w.manage_warning_stations inner join stations s on s.station_id = ws.station_id where w.group_receive_mail_id = ?";

        Connection connection = ds.getConnection();
        PreparedStatement stmUserInSite = null;
        PreparedStatement stmUserOutSite = null;
        PreparedStatement stmWarning = null;

        List<Object> data = new ArrayList<>();

        List<UserInfoReceiveMail> userInfoReceiveMails = new ArrayList<>();
        List<UserExpandReceiveMail> userExpandReceiveMails = new ArrayList<>();
        List<WarningRecipentReceiveMail> warningRecipentReceiveMails = new ArrayList<>();

        try{
            connection.setAutoCommit(false);
            stmUserInSite =  connection.prepareStatement(sqlUserInSite);
            stmUserOutSite = connection.prepareStatement(sqlUserOutSite);
            stmWarning = connection.prepareStatement(sqlWarning);

            stmUserInSite.setLong(1, id);

            ResultSet resultSetUserInSite = stmUserInSite.executeQuery();


            while (resultSetUserInSite.next()) {
                UserInfoReceiveMail userInfoReceiveMail = UserInfoReceiveMail
                        .builder()
                        .id(resultSetUserInSite.getString("id"))
                        .name(resultSetUserInSite.getString("name"))
                        .email(resultSetUserInSite.getString("email"))
                        .build();
                userInfoReceiveMails.add(userInfoReceiveMail);
            }

            stmUserOutSite.setLong(1, id);

            ResultSet resultSetUserOutSite = stmUserOutSite.executeQuery();


            while (resultSetUserOutSite.next()) {
                UserExpandReceiveMail userExpandReceiveMail = UserExpandReceiveMail
                        .builder()
                        .id(resultSetUserOutSite.getString("id"))
                        .name(resultSetUserOutSite.getString("name"))
                        .email(resultSetUserOutSite.getString("email"))
                        .build();
                userExpandReceiveMails.add(userExpandReceiveMail);
            }

            stmWarning.setLong(1, id);

            ResultSet resultSetWarning = stmWarning.executeQuery();


            while (resultSetWarning.next()) {
                WarningRecipentReceiveMail warningRecipentReceiveMail = WarningRecipentReceiveMail
                        .builder()
                        .id(resultSetWarning.getLong("id"))
                        .stationId(resultSetWarning.getString("station_id"))
                        .stationName(resultSetWarning.getString("station_name"))
                        .warningManagerId(resultSetWarning.getLong("warning_id"))
                        .warningManagerCode(resultSetWarning.getString("code"))
                        .build();
                warningRecipentReceiveMails.add(warningRecipentReceiveMail);
            }
            data.add(userInfoReceiveMails);
            data.add(userExpandReceiveMails);
            data.add(warningRecipentReceiveMails);



        } catch (Exception e){
            e.printStackTrace();
            return data;
        } finally {
            if(stmUserInSite != null){
                stmUserInSite.close();
            }
            if(stmUserOutSite != null){
                stmUserOutSite.close();
            }
            if(stmWarning!=null){
                stmWarning.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return data;
    }

    @Override
    public DefaultResponseDTO editMailGroupConfig(MailGroupConFigVM mailGroupConFigVM,
                                                  List<UserInfoReceiveMail> userInfoReceiveMailDeletes,
                                                  List<UserInfoReceiveMail> userInfoReceiveMailInserts,
                                                  List<UserExpandReceiveMail> userExpandReceiveMailInserts,
                                                  List<UserExpandReceiveMail> userExpandReceiveMailDelete,
                                                  List<WarningRecipentReceiveMail> warningRecipentReceiveMailDeletes,
                                                  List<WarningRecipentReceiveMail> warningRecipentReceiveMailInsert) throws SQLException {
        Connection connection = ds.getConnection();
        String sqlUpdateGroupReceiveMail = "update group_receive_mail set name = ?, code = ?, description = ? , modify_at = sysdate , modify_by = ?, STATUS = ? where id = ?";
        String sqlDeleteInSite = "delete from group_receive_mail_detail where  id_group_receive_mail = ? and USER_INFO_ID = ? ";
        String sqlDeleteOut = "delete from group_receive_mail_detail where id_group_receive_mail = ? and user_info_expant = ?";
        String sqlDeleteWarning = "delete from warning_recipents where id = ?";

        String sqlInsertGroupReceiveMailDetail = "insert into GROUP_RECEIVE_MAIL_DETAIL (ID, ID_GROUP_RECEIVE_MAIL, USER_INFO_ID, USER_INFO_EXPANT) values (GROUP_RECEIVE_MAIL_DETAIL_SEQ.nextval, ?,?,?)";
        String sqlInsertWarningRecipents = "insert into warning_recipents (id, GROUP_RECEIVE_MAIL_ID, manage_warning_stations) values (WARNING_RECIPENTS_SEQ.nextval,?,?)";

        PreparedStatement stmUpdateGroupReceiveMail = null;
        PreparedStatement stmDeleteInSite = null;
        PreparedStatement stmDeleteOut = null;
        PreparedStatement stmDeleteWarning = null;
        PreparedStatement stmInsertGroupReceiveMailDetail = null;
        PreparedStatement stmInsertWarningRecipents = null;

        try{
            connection.setAutoCommit(false);
            stmUpdateGroupReceiveMail = connection.prepareStatement(sqlUpdateGroupReceiveMail);
            stmDeleteInSite = connection.prepareStatement(sqlDeleteInSite);
            stmDeleteOut = connection.prepareStatement(sqlDeleteOut);
            stmDeleteWarning = connection.prepareStatement(sqlDeleteWarning);
            stmInsertGroupReceiveMailDetail = connection.prepareStatement(sqlInsertGroupReceiveMailDetail);
            stmInsertWarningRecipents = connection.prepareStatement(sqlInsertWarningRecipents);


            stmUpdateGroupReceiveMail.setString(1,mailGroupConFigVM.getName());
            stmUpdateGroupReceiveMail.setString(2, mailGroupConFigVM.getCode());
            stmUpdateGroupReceiveMail.setString(3, mailGroupConFigVM.getDescription());
            stmUpdateGroupReceiveMail.setString(4, mailGroupConFigVM.getUser());
            stmUpdateGroupReceiveMail.setInt(5, mailGroupConFigVM.getStatus());
            stmUpdateGroupReceiveMail.setLong(6, Long.parseLong(mailGroupConFigVM.getId()));
            stmUpdateGroupReceiveMail.executeUpdate();

            for(UserInfoReceiveMail userInfoReceiveMail : userInfoReceiveMailDeletes){
                stmDeleteInSite.setLong(1, Long.parseLong(mailGroupConFigVM.getId()));
                stmDeleteInSite.setString(2, userInfoReceiveMail.getId());
                stmDeleteInSite.addBatch();
            }
            stmDeleteInSite.executeBatch();

            for(UserExpandReceiveMail userExpandReceiveMail : userExpandReceiveMailDelete){
                stmDeleteOut.setLong(1, Long.parseLong(mailGroupConFigVM.getId()));
                stmDeleteOut.setString(2, userExpandReceiveMail.getId());
                stmDeleteOut.addBatch();
            }
            stmDeleteOut.executeBatch();

            for(WarningRecipentReceiveMail warningRecipentReceiveMail : warningRecipentReceiveMailDeletes){
                stmDeleteWarning.setLong(1, warningRecipentReceiveMail.getId());
                stmDeleteWarning.addBatch();
            }
            stmDeleteWarning.executeBatch();

            for(UserInfoReceiveMail userInfoReceiveMail : userInfoReceiveMailInserts){
                stmInsertGroupReceiveMailDetail.setLong(1, Long.parseLong(mailGroupConFigVM.getId()));
                stmInsertGroupReceiveMailDetail.setString(2,userInfoReceiveMail.getName());
                stmInsertGroupReceiveMailDetail.setNull(3, Types.BIGINT);
                stmInsertGroupReceiveMailDetail.addBatch();

            }
            stmInsertGroupReceiveMailDetail.executeBatch();
            for(UserExpandReceiveMail userExpandReceiveMail : userExpandReceiveMailInserts){
                stmInsertGroupReceiveMailDetail.setLong(1, Long.parseLong(mailGroupConFigVM.getId()));
                stmInsertGroupReceiveMailDetail.setNull(2, Types.CHAR);
                stmInsertGroupReceiveMailDetail.setString(3,userExpandReceiveMail.getId());
                stmInsertGroupReceiveMailDetail.addBatch();
            }
            stmInsertGroupReceiveMailDetail.executeBatch();

            for(WarningRecipentReceiveMail warningRecipentReceiveMail :warningRecipentReceiveMailInsert ){
                stmInsertWarningRecipents.setLong(1, Long.parseLong(mailGroupConFigVM.getId()));
                stmInsertWarningRecipents.setLong(2,warningRecipentReceiveMail.getWarningManagerId());
                stmInsertWarningRecipents.addBatch();

            }
            stmInsertWarningRecipents.executeBatch();
            connection.commit();

        } catch (Exception e){

            e.printStackTrace();

        } finally {
            if(stmUpdateGroupReceiveMail != null){
                stmUpdateGroupReceiveMail.close();
            }

            if(stmDeleteInSite != null){
                stmDeleteInSite.close();
            }
            if(stmDeleteOut !=null){
                stmDeleteOut.close();
            }
            if(stmDeleteWarning != null){
                stmDeleteWarning.close();
            }
           if(stmInsertGroupReceiveMailDetail!=null){
               stmInsertGroupReceiveMailDetail.close();
           }
           if(stmInsertWarningRecipents != null){
               stmInsertWarningRecipents.close();
           }
           connection.close();
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }
}
