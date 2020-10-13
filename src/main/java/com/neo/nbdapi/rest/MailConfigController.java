package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMailConfigVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.EditMailConfigVM;
import com.neo.nbdapi.services.MailConfigService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_MAIL_CONFIG)
public class MailConfigController {

    @Autowired
    private MailConfigService mailConfigService;

    /**
     * Api get list mail config pagination
     * @param defaultRequestPagingVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @PostMapping("/get-list-mail-config-pagination")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return mailConfigService.getListMailConfigPagination(defaultRequestPagingVM);
    }

    /**
     * Api create mail config
     * @param createMailConfigVM
     * @return
     * @throws SQLException
     */
    @PostMapping("/create-mail-config")
    public DefaultResponseDTO createMailConfig(@RequestBody @Valid CreateMailConfigVM createMailConfigVM) throws SQLException {
        return mailConfigService.createMailConfig(createMailConfigVM);
    }

    /**
     * api edit mail config
     * @param editMailConfigVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @PutMapping("/edit-mail-config")
    public DefaultResponseDTO editMailConfig(@RequestBody @Valid EditMailConfigVM editMailConfigVM) throws SQLException, BusinessException {
        return mailConfigService.editMailConfig(editMailConfigVM);
    }

    /**
     * api delete mail config
     * @param defaultDeleteVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @DeleteMapping("/delete-mail-config")
    public DefaultResponseDTO deleteMailConfig(@RequestBody @Valid DefaultDeleteVM defaultDeleteVM) throws SQLException, BusinessException {
        return mailConfigService.deleteMailConfig(defaultDeleteVM);
    }
}
