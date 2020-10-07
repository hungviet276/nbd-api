package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.MailConfigService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_MAIL_CONFIG)
public class UsersManagerController {

    @Autowired
    private MailConfigService mailConfigService;

    @PostMapping("/get-list-mail-config-pagination")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return mailConfigService.getListMailConfigPagination(defaultRequestPagingVM);
    }

    @PostMapping("/create-mail-config")
    public DefaultResponseDTO createMailConfig(@RequestBody @Valid CreateMailConfigVM createMailConfigVM) throws SQLException {
        return mailConfigService.createMailConfig(createMailConfigVM);
    }
}
