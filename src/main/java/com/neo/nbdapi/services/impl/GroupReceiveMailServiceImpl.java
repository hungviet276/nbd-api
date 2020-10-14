package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.GroupMailReceiveDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.GroupMailReceive;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.EditMailConfigVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveVM;
import com.neo.nbdapi.services.GroupMailReceiveService;
import com.neo.nbdapi.services.objsearch.SearchGroupMailReceive;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupReceiveMailServiceImpl implements GroupMailReceiveService {
    private Logger logger = LogManager.getLogger(MailConfigServiceImpl.class);

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private GroupMailReceiveDAO groupMailReceiveDAO;

    @Override
    public DefaultPaginationDTO getGroupReceiveMailsPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        //select id,code,name,status,description from group_receive_mail
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
    public DefaultResponseDTO createGroupReceiveMails(GroupMailReceiveVM groupMailReceiveVM) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "insert into GROUP_RECEIVE_MAIL(ID,NAME,CODE,STATUS,DESCRIPTION)values(GROUP_RECEIVE_MAIL_SEQ.nextval,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, groupMailReceiveVM.getName());
            statement.setString(2, groupMailReceiveVM.getCode());
            statement.setInt(3, groupMailReceiveVM.getStatus());
            statement.setString(4, groupMailReceiveVM.getDescription());
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        }
    }

    @Override
    public DefaultResponseDTO editGroupReceiveMail(GroupMailReceiveVM groupMailReceiveVM) throws SQLException, BusinessException {
        GroupMailReceive groupMailReceive = groupMailReceiveDAO.findGroupMailReceiveConfigById(groupMailReceiveVM.getId());
        if (groupMailReceive == null)
            throw new BusinessException("Không tồn tại bản ghi trong hệ thống");
        GroupMailReceive tmp = GroupMailReceive.builder()
                .id(groupMailReceiveVM.getId())
                .code(groupMailReceiveVM.getCode())
                .status(groupMailReceiveVM.getStatus())
                .name(groupMailReceiveVM.getName())
                .description(groupMailReceiveVM.getDescription())
                .build();
        logger.debug("Group  Mail Service: {}", tmp);
        groupMailReceiveDAO.editGroupMailReceive(tmp);
        return new DefaultResponseDTO(1, "Sửa cấu hình email thành công");
    }

    @Override
    public DefaultResponseDTO deleteGroupReceiveMail(DefaultDeleteVM defaultDeleteVM) throws SQLException, BusinessException {
        long countMailConfig = groupMailReceiveDAO.countGroupMailReceiveById(Long.parseLong(defaultDeleteVM.getId()));
        if (countMailConfig < 1)
            throw new BusinessException("Không tìm thấy cấu hình email");

        groupMailReceiveDAO.delete(Long.parseLong(defaultDeleteVM.getId()));
        return new DefaultResponseDTO(1, "Thành công");

    }


}
