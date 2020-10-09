package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
public interface LogActService {
    DefaultPaginationDTO getListLogActPagination(DefaultRequestPagingVM defaultRequestPagingVM);
}
