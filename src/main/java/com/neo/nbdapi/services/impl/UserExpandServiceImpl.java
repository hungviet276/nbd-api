package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dao.UserInFoExpandDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.NameUserDTO;
import com.neo.nbdapi.dto.SelectGroupDTO;
import com.neo.nbdapi.entity.UserInfoExpand;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.UserExpandService;
import com.neo.nbdapi.services.objsearch.SearchUsesExpand;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Service
public class UserExpandServiceImpl implements UserExpandService {

    private Logger logger = LogManager.getLogger(UserExpandServiceImpl.class);

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private UserInFoExpandDAO userInFoExpandDAO;

    @Override
    public DefaultPaginationDTO getListMailConfigPagination(@Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<UserInfoExpand> userInfoExpands = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();
            StringBuilder sql = new StringBuilder("select id, name, mobile, code, email, gender, status, card_number, position from user_info_expand where 1=1 ");
            logger.debug("Object search: {}", search);
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                SearchUsesExpand objectSearch = objectMapper.readValue(search, SearchUsesExpand.class);
                if (Strings.isNotEmpty(objectSearch.getId())) {
                    sql.append(" AND id = ? ");
                    paramSearch.add(objectSearch.getId());
                }
                if (Strings.isNotEmpty(objectSearch.getName())) {
                    sql.append(" AND UPPER(name) like ? ");
                    paramSearch.add("%"+objectSearch.getName().toUpperCase()+"%");
                }
                if (Strings.isNotEmpty(objectSearch.getPhone())) {
                    sql.append(" AND mobile like ? ");
                    paramSearch.add("%"+objectSearch.getPhone()+"%");
                }
                if (Strings.isNotEmpty(objectSearch.getCode())) {
                    sql.append(" AND UPPER(code) like ? ");
                    paramSearch.add("%"+objectSearch.getCode().toUpperCase()+"%");
                }
                if (Strings.isNotEmpty(objectSearch.getEmail())) {
                    sql.append(" AND UPPER(email) like ? ");
                    paramSearch.add("%"+objectSearch.getEmail().toUpperCase()+"%");
                }
                if (Strings.isNotEmpty(objectSearch.getSex())) {
                    sql.append(" AND gender = ? ");
                    paramSearch.add(objectSearch.getSex());
                }
                if (Strings.isNotEmpty(objectSearch.getStatus())) {
                    sql.append(" AND status = ? ");
                    paramSearch.add(objectSearch.getStatus());
                }
                if (Strings.isNotEmpty(objectSearch.getCardId())) {
                    sql.append(" AND card_number like ? ");
                    paramSearch.add("%"+objectSearch.getStatus()+"%");
                }
                if (Strings.isNotEmpty(objectSearch.getPosition())) {
                    sql.append(" AND position like ? ");
                    paramSearch.add("%"+objectSearch.getPosition()+"%");
                }
                sql.append(" order by created_date desc");
            }
            System.out.println("sql---------------" +sql);
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());

            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);
            while (resultSetListData.next()) {
                UserInfoExpand userInfo = UserInfoExpand.builder()
                        .id(Long.parseLong(resultSetListData.getString("id")))
                        .name(resultSetListData.getString("name"))
                        .mobile(resultSetListData.getString("mobile"))
                        .code(resultSetListData.getString("code"))
                        .email(resultSetListData.getString("email"))
                        .gender(resultSetListData.getInt("gender"))
                        .status(resultSetListData.getInt("status"))
                        .cardNumber(resultSetListData.getString("card_number"))
                        .position(resultSetListData.getString("position"))
                        .build();
                userInfoExpands.add(userInfo);

            }
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(userInfoExpands.size())
                    .recordsTotal(total)
                    .content(userInfoExpands)
                    .build();
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(userInfoExpands)
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(userInfoExpands)
                    .build();
        }
    }

    @Override
    public List<NameUserDTO> getNameUser(SelectGroupDTO selectGroupDTO) throws SQLException {
        return userInFoExpandDAO.getNameUser(selectGroupDTO);
    }

}
