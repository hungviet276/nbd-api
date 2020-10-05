package com.neo.nbdapi.rest;

import java.sql.SQLException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neo.nbdapi.dto.GroupRecieveMailDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.utils.Constants;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_GROUP_MAIL_RECEIVE)
public class GroupMailReceiveController {
	@GetMapping("/group-mail")
    public GroupRecieveMailDTO getListMailConfigPagination(GroupRecieveMailDTO groupRecieveMail) throws SQLException, BusinessException {
        return groupRecieveMail;
    }

}
