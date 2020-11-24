package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.GroupMailReceiveDetailDAO;
import com.neo.nbdapi.dao.MailGroupConfigDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.GroupMailReceive;
import com.neo.nbdapi.entity.UserExpandReceiveMail;
import com.neo.nbdapi.entity.UserInfoReceiveMail;
import com.neo.nbdapi.entity.WarningRecipentReceiveMail;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;
import com.neo.nbdapi.services.MailGroupConfigService;
import com.neo.nbdapi.services.objsearch.SearchGroupMailReceive;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MailGroupConfigServiceImpl implements MailGroupConfigService {
    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private MailGroupConfigDAO mailGroupConfigDAO;

    @Override
    public DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        List<GroupMailReceive> groupReceives = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            StringBuilder sql = new StringBuilder("select id,code,name,status,description from group_receive_mail where 1 = 1");
            String search = defaultRequestPagingVM.getSearch();
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchGroupMailReceive objectSearch = objectMapper.readValue(search, SearchGroupMailReceive.class);
                    if (Strings.isNotEmpty(objectSearch.getId())) {
                        sql.append(" AND id = ? ");
                        paramSearch.add(objectSearch.getId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getCode())) {
                        sql.append(" AND code like ? ");
                        paramSearch.add("%" + objectSearch.getCode() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getName())) {
                        sql.append(" AND name like ? ");
                        paramSearch.add("%" + objectSearch.getName() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getStatus())) {
                        sql.append(" AND status =  ? ");
                        paramSearch.add(objectSearch.getStatus());
                    }
                    if (Strings.isNotEmpty(objectSearch.getDescription())) {
                        sql.append(" AND description like ? ");
                        paramSearch.add("%" + objectSearch.getDescription() + "%");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                GroupMailReceive groupReceive = GroupMailReceive.builder()
                        .id(resultSetListData.getLong("id"))
                        .code(resultSetListData.getString("code"))
                        .status(resultSetListData.getInt("status"))
                        .name(resultSetListData.getString("name"))
                        .description(resultSetListData.getString("description"))
                        .build();
                groupReceives.add(groupReceive);
            }
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(groupReceives.size())
                    .recordsTotal(total)
                    .content(groupReceives)
                    .build();

        } catch (Exception e){

            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(groupReceives)
                    .build();
        }
    }

    @Override
    public DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException {
        return mailGroupConfigDAO.createMailGroupConfig(mailGroupConFigVM);
    }

    @Override
    public List<Object> getInfoMailReceive(Long id) throws SQLException {
        return mailGroupConfigDAO.getInfoMailReceive(id);
    }

    @Override
    public DefaultResponseDTO editMailGroupConfig(MailGroupConFigVM mailGroupConFigVM) throws SQLException {
        List<Object> datas = mailGroupConfigDAO.getInfoMailReceive(Long.parseLong(mailGroupConFigVM.getId()));
        List<UserInfoReceiveMail> userInfoReceiveMails = (List<UserInfoReceiveMail>) datas.get(0);
        List<UserExpandReceiveMail> userExpandReceiveMails = (List<UserExpandReceiveMail>) datas.get(1);
        List<WarningRecipentReceiveMail> warningRecipentReceiveMails = (List<WarningRecipentReceiveMail>) datas.get(2);

        List<UserInfoReceiveMail> UserInfoReceiveMailInserts = new ArrayList<>();
        List<UserInfoReceiveMail> UserInfoReceiveMailDeletes = new ArrayList<>();

        List<UserExpandReceiveMail> userExpandReceiveMailInserts = new ArrayList<>();
        List<UserExpandReceiveMail> userExpandReceiveMailDeletes = new ArrayList<>();
        List<WarningRecipentReceiveMail> warningRecipentReceiveMailInserts = new ArrayList<>();
        List<WarningRecipentReceiveMail> warningRecipentReceiveMailDeletes = new ArrayList<>();

        // lấy ra các user info cần thêm mới
        for(String  userInfoTmp : mailGroupConFigVM.getUserInSites()){
            Boolean isInsert = true;
            for(UserInfoReceiveMail userInfoReceiveMail : userInfoReceiveMails){
                if(userInfoTmp.equals(userInfoReceiveMail.getName())){
                    isInsert = false;
                    break;
                }
            }
            if(isInsert){
                UserInfoReceiveMailInserts.add(UserInfoReceiveMail.builder().name(userInfoTmp).build());
            }
        }
        // lấy ra các user info cần xóa
        for(UserInfoReceiveMail userInfoReceiveMail : userInfoReceiveMails){
            Boolean isDell = true;
            for(String  userInfoTmp : mailGroupConFigVM.getUserInSites()){
                if(userInfoReceiveMail.getName().equals(userInfoTmp)){
                    isDell = false;
                    break;
                }
            }
            if(isDell){
                UserInfoReceiveMailDeletes.add(userInfoReceiveMail);
            }
        }

        //lấy ra các user ngoài hệ thống cần thêm mới
        for(String idExpand : mailGroupConFigVM.getUserOutSite()){
            Boolean insert = true;
            for(UserExpandReceiveMail userExpandReceiveMail :userExpandReceiveMails){
                if(userExpandReceiveMail.getId().equals(idExpand)){
                    insert = false;
                    break;
                }
            }
            if(insert){
                userExpandReceiveMailInserts.add(UserExpandReceiveMail.builder().id(idExpand).build());
            }
        }

        // lấy ra các user expand cần xóa

        for(UserExpandReceiveMail userExpandReceiveMail :userExpandReceiveMails){
            Boolean delete = true;
            for(String idExpand : mailGroupConFigVM.getUserOutSite()){
                if(userExpandReceiveMail.getId().equals(idExpand)){
                    delete = false;
                    break;
                }
            }
            if(delete){
                userExpandReceiveMailDeletes.add(userExpandReceiveMail);
            }

        }

        // sánh sách các cảnh báo được insert   List<WarningRecipentReceiveMail> warningRecipentReceiveMails

        for(String warning : mailGroupConFigVM.getWarningConfig()){
           boolean insert = true;
            for(WarningRecipentReceiveMail  warningRecipentReceiveMail :  warningRecipentReceiveMails){
                if(warningRecipentReceiveMail.getWarningManagerId() == Long.parseLong(warning)){
                    insert = false;
                    break;
                }
            }
            if(insert){
                warningRecipentReceiveMailInserts.add(WarningRecipentReceiveMail.builder().warningManagerId(Long.parseLong(warning)).build());
            }

        }

        for(WarningRecipentReceiveMail  warningRecipentReceiveMail :  warningRecipentReceiveMails){
            boolean delete = true;
            for(String warning : mailGroupConFigVM.getWarningConfig()){
                if(warningRecipentReceiveMail.getWarningManagerId() == Long.parseLong(warning)){
                    delete = false;
                    break;
                }
            }
            if(delete){
                warningRecipentReceiveMailDeletes.add(warningRecipentReceiveMail);
            }

        }
        return mailGroupConfigDAO.editMailGroupConfig(mailGroupConFigVM, UserInfoReceiveMailDeletes, UserInfoReceiveMailInserts,
                userExpandReceiveMailInserts, userExpandReceiveMailDeletes, warningRecipentReceiveMailDeletes, warningRecipentReceiveMailInserts);
    }

    @Override
    public DefaultResponseDTO deleteMailGroupConfig(Long id) throws SQLException {
        return mailGroupConfigDAO.deleteMailGroupConfig(id);
    }
}
