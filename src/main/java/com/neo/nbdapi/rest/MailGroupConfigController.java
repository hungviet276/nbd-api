package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.GroupMailReceiveService;
import com.neo.nbdapi.services.MailGroupConfigService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_GROUP_MAIL_CONFIG)
public class MailGroupConfigController {
    @Autowired
    private MailGroupConfigService mailGroupConfigService;

    @PostMapping("/get-list-group-mail-paging-nation")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return mailGroupConfigService.getGroupReceiveMailsPagination(defaultRequestPagingVM);
    }
}
