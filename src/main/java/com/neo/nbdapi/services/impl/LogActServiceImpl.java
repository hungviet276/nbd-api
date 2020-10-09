package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.LogActService;
import org.springframework.stereotype.Service;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */

@Service
public class LogActServiceImpl implements LogActService {
    @Override
    public DefaultPaginationDTO getListLogActPagination(DefaultRequestPagingVM defaultRequestPagingVM) {
        return null;
    }
}
