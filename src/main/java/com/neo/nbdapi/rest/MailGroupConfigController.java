package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;
import com.neo.nbdapi.services.MailGroupConfigService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_GROUP_MAIL_CONFIG)
public class MailGroupConfigController {
    @Autowired
    private MailGroupConfigService mailGroupConfigService;

    @PostMapping("/get-list-group-mail-paging-nation")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return mailGroupConfigService.getGroupReceiveMailsPagination(defaultRequestPagingVM);
    }

    @PostMapping
    public DefaultResponseDTO getListMailConfigPagination(@RequestBody @Valid MailGroupConFigVM mailGroupConFigVM) throws SQLException, BusinessException {
        return mailGroupConfigService.createMailGroupConfig(mailGroupConFigVM);
    }

    @GetMapping("/get-info")
    public List<Object> getListMailConfigPagination(@RequestParam  Long id) throws SQLException {
        return mailGroupConfigService.getInfoMailReceive(id);
    }
    @PutMapping
    public DefaultResponseDTO editListMailConfigPagination(@RequestBody @Valid MailGroupConFigVM mailGroupConFigVM) throws SQLException {
        return mailGroupConfigService.editMailGroupConfig(mailGroupConFigVM);
    }
    @DeleteMapping
    public DefaultResponseDTO editMailGroupConfig(@RequestParam  Long id) throws SQLException{
        return mailGroupConfigService.deleteMailGroupConfig(id);
    }

}
