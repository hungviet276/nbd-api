package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.MailConfigDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.EditMailConfigVM;
import com.neo.nbdapi.services.MailConfigService;
import com.neo.nbdapi.services.objsearch.SearchMailConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class MailConfigServiceImpl implements MailConfigService {

    private Logger logger = LogManager.getLogger(MailConfigServiceImpl.class);

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private MailConfigDAO mailConfigDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    /**
     * method get list mail config pagination
     * @param defaultRequestPagingVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @Override
    public DefaultPaginationDTO getListMailConfigPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<MailConfig> mailConfigList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            logger.debug("mailConfigVM: {}", defaultRequestPagingVM);
            // start = pageNumber, lenght = recordPerPage
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("SELECT id, ip, port, username, password, domain, sender_name, email_address, protocol FROM mail_config  WHERE 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set value query to sql
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchMailConfig objectSearch = objectMapper.readValue(search, SearchMailConfig.class);
                    if (Strings.isNotEmpty(objectSearch.getId())) {
                        sql.append(" AND id = ? ");
                        paramSearch.add(objectSearch.getId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getIp())) {
                        sql.append(" AND ip LIKE ? ");
                        paramSearch.add("%" + objectSearch.getIp() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getPort())) {
                        sql.append(" AND port = ? ");
                        paramSearch.add(objectSearch.getPort());
                    }
                    if (Strings.isNotEmpty(objectSearch.getUsername())) {
                        sql.append(" AND username = ? ");
                        paramSearch.add(objectSearch.getUsername());
                    }
                    if (Strings.isNotEmpty(objectSearch.getPassword())) {
                        sql.append(" AND password = ? ");
                        paramSearch.add(objectSearch.getPassword());
                    }
                    if (Strings.isNotEmpty(objectSearch.getDomain())) {
                        sql.append(" AND domain = ? ");
                        paramSearch.add(objectSearch.getDomain());
                    }
                    if (Strings.isNotEmpty(objectSearch.getSenderName())) {
                        sql.append(" AND sender_name = ? ");
                        paramSearch.add(objectSearch.getSenderName());
                    }
                    if (Strings.isNotEmpty(objectSearch.getEmail())) {
                        sql.append(" AND email_address = ? ");
                        paramSearch.add(objectSearch.getEmail());
                    }
                    if (Strings.isNotEmpty(objectSearch.getProtocol())) {
                        sql.append(" AND protocol = ? ");
                        paramSearch.add(objectSearch.getProtocol());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sql.append(" ORDER BY id DESC ");
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            // get list result by pagination
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                MailConfig mailConfig = MailConfig.builder()
                        .id(resultSetListData.getInt("id"))
                        .ip(resultSetListData.getString("ip"))
                        .port(resultSetListData.getString("port"))
                        .username(resultSetListData.getString("username"))
                        .password(resultSetListData.getString("password"))
                        .domain(resultSetListData.getString("domain"))
                        .senderName(resultSetListData.getString("sender_name"))
                        .emailAddress(resultSetListData.getString("email_address"))
                        .protocol(resultSetListData.getString("protocol"))
                        .build();
                mailConfigList.add(mailConfig);
            }

            // count totalElements
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(mailConfigList.size())
                    .recordsTotal(total)
                    .content(mailConfigList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(mailConfigList)
                    .build();
        }
    }

    /**
     * method create mail config
     * @param createMailConfigVM
     * @return
     * @throws SQLException
     */
    @Override
    public DefaultResponseDTO createMailConfig(CreateMailConfigVM createMailConfigVM) throws SQLException {
        // do insert mailConfig
            MailConfig mailConfig = MailConfig.builder()
                    .ip(createMailConfigVM.getIp())
                    .port(createMailConfigVM.getPort())
                    .username(createMailConfigVM.getUsername())
                    .password(createMailConfigVM.getPassword())
                    .domain(createMailConfigVM.getDomain())
                    .senderName(createMailConfigVM.getSenderName())
                    .emailAddress(createMailConfigVM.getEmail())
                    .protocol(createMailConfigVM.getProtocol())
                    .build();
            mailConfigDAO.createMailConfig(mailConfig);
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
    }

    /**
     * method edit mail config
     * @param editMailConfigVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @Override
    public DefaultResponseDTO editMailConfig(EditMailConfigVM editMailConfigVM) throws SQLException, BusinessException {
        // check mail config id is exists
        MailConfig mailConfig = mailConfigDAO.findMailConfigById(Long.parseLong(editMailConfigVM.getId()));
        // if not exists => can not edit mail config, throw exception
        if (mailConfig == null)
            throw new BusinessException("Cấu hình email không tồn tại");
        MailConfig mailConfigEdit = mailConfig.toBuilder()
                .ip(editMailConfigVM.getIp())
                .port(editMailConfigVM.getPort())
                .username(editMailConfigVM.getUsername())
                .password(editMailConfigVM.getPassword())
                .domain(editMailConfigVM.getDomain())
                .senderName(editMailConfigVM.getSenderName())
                .emailAddress(editMailConfigVM.getEmail())
                .protocol(editMailConfigVM.getProtocol())
                .build();
        logger.debug("MailConfigEdit: {}", mailConfigEdit);
        mailConfigDAO.editMailConfig(mailConfigEdit);
        return new DefaultResponseDTO(1, "Sửa cấu hình email thành công");
    }

    /**
     * method delete mail config
     * @param defaultDeleteVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @Override
    public DefaultResponseDTO deleteMailConfig(DefaultDeleteVM defaultDeleteVM) throws SQLException, BusinessException {
        // count result search of pagination, totalElements
        long countMailConfig = mailConfigDAO.countMailConfigById(Long.parseLong(defaultDeleteVM.getId()));
        if (countMailConfig < 1)
            throw new BusinessException("Không tìm thấy cấu hình email");

        mailConfigDAO.delete(Long.parseLong(defaultDeleteVM.getId()));
        return new DefaultResponseDTO(1, "Thành công");
    }
}
