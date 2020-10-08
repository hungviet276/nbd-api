package com.neo.nbdapi.rest;

import java.sql.SQLException;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.EditMailConfigVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveVM;
import com.neo.nbdapi.services.GroupMailReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.utils.Constants;

import javax.validation.Valid;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_GROUP_MAIL_RECEIVE)
public class GroupMailReceiveController {
    @Autowired
    private GroupMailReceiveService groupMailReceiveService;

    @PostMapping("/get-list-group-mail-paging-nation")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return groupMailReceiveService.getGroupReceiveMailsPagination(defaultRequestPagingVM);
    }

    @PostMapping
    public DefaultResponseDTO createMailReceive(@RequestBody @Valid GroupMailReceiveVM groupMailReceiveVM) throws SQLException, BusinessException {
        return groupMailReceiveService.createGroupReceiveMails(groupMailReceiveVM);
    }
    @PutMapping
    public DefaultResponseDTO editMailConfig(@RequestBody @Valid GroupMailReceiveVM editMailConfigVM) throws SQLException, BusinessException {
        return groupMailReceiveService.editGroupReceiveMail(editMailConfigVM);
    }

    @DeleteMapping
    public DefaultResponseDTO deleteMailConfig(@RequestBody @Valid DefaultDeleteVM defaultDeleteVM) throws SQLException, BusinessException {
        return groupMailReceiveService.deleteGroupReceiveMail(defaultDeleteVM);
    }
}
