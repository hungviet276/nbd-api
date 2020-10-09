package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.LogActService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@RestController(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_MAIL_CONFIG)
public class LogActController {

    @Autowired
    private LogActService logActService;

    /**
     * Api get list log act pagination
     * @param defaultRequestPagingVM
     * @return
     */
    @PostMapping("/get-list-log-act-pagination")
    public DefaultPaginationDTO getListLogActPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) {
        return logActService.getListLogActPagination(defaultRequestPagingVM);
    }
}
