package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.MailConfigService;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private HikariDataSource ds;

    @Override
    public DefaultPaginationDTO getListMailConfigPagination(DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String sql = "SELECT id, ip, port, username, password, domain, sender_name, email_address, protocol FROM mail_config";
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql, pageNumber + 1, recordPerPage);
            List<MailConfig> mailConfigList = new ArrayList<>();
            while(resultSetListData.next()) {
                MailConfig mailConfig = MailConfig.builder()
                        .id(resultSetListData.getInt(1))
                        .ip(resultSetListData.getString(2))
                        .port(resultSetListData.getString(3))
                        .username(resultSetListData.getString(4))
                        .password(resultSetListData.getString(5))
                        .domain(resultSetListData.getString(6))
                        .senderName(resultSetListData.getString(7))
                        .emailAddress(resultSetListData.getString(8))
                        .protocol(resultSetListData.getString(9))
                        .build();
                mailConfigList.add(mailConfig);
            }

            // count result
            long total = paginationDAO.countResultQuery("SELECT id, ip, port, username, password, domain, sender_name, email_address, protocol FROM mail_config");
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(mailConfigList.size())
                    .recordsTotal(total)
                    .content(mailConfigList)
                    .build();
        }
    }

    @Override
    public DefaultResponseDTO createMailConfig(CreateMailConfigVM createMailConfigVM) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = "INSERT INTO mail_config(id, ip, port, username, password, domain, sender_name, email_address, protocol) values (MAIL_CONFIG_SEQ.nextval, ?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, createMailConfigVM.getIp());
            statement.setString(2, createMailConfigVM.getPort());
            statement.setString(3, createMailConfigVM.getUsername());
            statement.setString(4, createMailConfigVM.getPassword());
            statement.setString(5, createMailConfigVM.getDomain());
            statement.setString(6, createMailConfigVM.getSenderName());
            statement.setString(7, createMailConfigVM.getEmailAddress());
            statement.setString(8, createMailConfigVM.getProtocol());
            statement.execute();
            DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
            return defaultResponseDTO;
        }
    }
}
