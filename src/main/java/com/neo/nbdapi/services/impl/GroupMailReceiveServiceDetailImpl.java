package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.GroupMailReceiveDetailDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.entity.GroupMailReceiveDetail;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveDetailVM;
import com.neo.nbdapi.services.GroupMailReceiveServiceDetail;
import com.neo.nbdapi.services.objsearch.SearchGroupMailReceiveDetail;
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
public class GroupMailReceiveServiceDetailImpl implements GroupMailReceiveServiceDetail {
    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private GroupMailReceiveDetailDAO groupMailReceiveDetailDAO;

    @Override
    public DefaultPaginationDTO getGroupReceiveMailDetailPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        List<GroupMailReceiveDetail> groupReceiveDetails = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            StringBuilder sql = new StringBuilder("select gd.id, u.name , g.name as group_name from group_receive_mail_detail gd inner join user_info u  on gd.user_info_id = u.id inner join group_receive_mail g on g.id = gd.id_group_receive_mail where 1=1");
            String search = defaultRequestPagingVM.getSearch();
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try{
                    SearchGroupMailReceiveDetail objectSearch = objectMapper.readValue(search, SearchGroupMailReceiveDetail.class);
                    if (objectSearch.getId() != null) {
                        sql.append(" AND gd.id = ? ");
                        paramSearch.add(objectSearch.getId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getName())) {
                        sql.append(" AND u.name like ? ");
                        paramSearch.add("%" + objectSearch.getName() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getGroupName())) {
                        sql.append(" AND g.name like ? ");
                        paramSearch.add("%" + objectSearch.getGroupName() + "%");
                    }
                    if (objectSearch.getIdGroup() != null) {
                        sql.append(" AND g.id = ?");
                        paramSearch.add( objectSearch.getIdGroup());
                    }
                    sql.append(" order by u.name");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                GroupMailReceiveDetail groupMailReceiveDetail = GroupMailReceiveDetail.builder()
                        .id(resultSetListData.getLong("id"))
                        .name(resultSetListData.getString("name"))
                        .groupName(resultSetListData.getString("group_name"))
                        .build();
                groupReceiveDetails.add(groupMailReceiveDetail);
            }
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(groupReceiveDetails.size())
                    .recordsTotal(total)
                    .content(groupReceiveDetails)
                    .build();
        }catch (Exception e){
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(groupReceiveDetails)
                    .build();
        }

    }

    @Override
    public DefaultResponseDTO createGroupReceiveMailDetail(GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException {
        return groupMailReceiveDetailDAO.createGroupReceiveMailDetail(groupMailReceiveDetailVM);
    }

    @Override
    public DefaultResponseDTO editGroupReceiveMailDetail(GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException {
        // lấy ra danh sách thực tế đang có trong cơ sở dữ liệu
        List<String> idUsers = groupMailReceiveDetailDAO.getAllUserById(groupMailReceiveDetailVM.getId());

        List<String> userIdReseives = groupMailReceiveDetailVM.getUserReceive();

        List<String> idUsersAdds = new ArrayList<>();

        List<String> idUserDells = new ArrayList<>();

        // so sánh danh sách thực tê với danh sách truyền vào để quyết định xem cái nào sẽ bị xóa cái nào sẽ được thêm mới
        if(groupMailReceiveDetailVM.getUserReceive() == null){
            return groupMailReceiveDetailDAO.deleteGroupReceiveMailDetail(groupMailReceiveDetailVM.getId());
        }
        for (String receiveDb :idUsers) {
            String tmp ="";
            for (String receive:userIdReseives) {
                if(receiveDb.equals(receive)){
                    tmp.equals(receive);
                    break;
                }
            }
            if(tmp.equals("")){
                idUserDells.add(receiveDb);
            }

        }
        for (String receive:userIdReseives) {
            String tmp ="";
            for (String receiveDb :idUsers) {
                if(receiveDb.equals(receive)){
                    tmp.equals(receiveDb);
                    break;
                }
            }
            if(tmp.equals("")){
                idUsersAdds.add(receive);
            }

        }
        return groupMailReceiveDetailDAO.editGroupReceiveMailDetail(idUserDells,idUsersAdds,groupMailReceiveDetailVM.getIdGroup());
    }

    @Override
    public DefaultResponseDTO deleteGroupReceiveMailDetail(GroupDetail groupDetail) throws SQLException {
        return groupMailReceiveDetailDAO.deleteGroupReceiveMailDetail(groupDetail.getIdDetail());
    }


}
