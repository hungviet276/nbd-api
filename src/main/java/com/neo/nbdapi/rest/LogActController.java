package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.LogActService;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_LOG_ACT)
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

    /**
     * Api get list api of user access to view log detail
     * @return List<MenuDTO>
     */
    @GetMapping("/get-list-menu-view-log-of-user")
    public List<MenuDTO> getListMenuViewLogOfUser() throws SQLException {
        return logActService.getListMenuViewLogOfUser();
    }

    /**
     * Api export log act
     * @param searchLogAct
     * @return ResponseEntity<Resource>
     */
    @PostMapping("/export")
    public ResponseEntity<Resource> exportLogAct(@RequestBody @Valid SearchLogAct searchLogAct) throws SQLException {
        return logActService.export(searchLogAct);
    }
}
