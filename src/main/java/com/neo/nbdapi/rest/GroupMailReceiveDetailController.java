package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.GroupDetail;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.GroupMailReceiveDetailVM;
import com.neo.nbdapi.services.GroupMailReceiveServiceDetail;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_GROUP_MAIL_RECEIVE_DETAIL)
public class GroupMailReceiveDetailController {
    @Autowired
    private GroupMailReceiveServiceDetail groupMailReceiveServiceDetail;

    @PostMapping("/get-list-group-mail-detail-paging-nation")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return groupMailReceiveServiceDetail.getGroupReceiveMailDetailPagination(defaultRequestPagingVM);
    }
    @PostMapping
    public DefaultResponseDTO createMailReceiveDetail(@RequestBody @Valid GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException, BusinessException {
        return groupMailReceiveServiceDetail.createGroupReceiveMailDetail(groupMailReceiveDetailVM);
    }

    @PutMapping
    public DefaultResponseDTO editMailReceiveDetail(@RequestBody @Valid GroupMailReceiveDetailVM groupMailReceiveDetailVM) throws SQLException, BusinessException {
        return groupMailReceiveServiceDetail.editGroupReceiveMailDetail(groupMailReceiveDetailVM);
    }

    @DeleteMapping
    public DefaultResponseDTO getUserInfoByGroup(@RequestBody GroupDetail groupDetail) throws SQLException {
        return groupMailReceiveServiceDetail.deleteGroupReceiveMailDetail(groupDetail);
    }
}
