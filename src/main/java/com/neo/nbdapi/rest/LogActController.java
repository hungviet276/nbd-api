package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.MenuDTO;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.LogActService;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.DateUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
     */
    @PostMapping("/export")
    public void exportLogAct(@RequestBody @Valid SearchLogAct searchLogAct, HttpServletResponse response) throws SQLException, IOException {
        SXSSFWorkbook workbook = logActService.export(searchLogAct);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String fileName = Constants.LOG_ACT.FILE_NAME_EXPORT_LOG_ACT + "_" + DateUtils.getDateAndTimeFileName() + ".xlsx";
        try {
            workbook.write(byteArrayOutputStream);
            byte[] outArray = byteArrayOutputStream.toByteArray();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setContentLength(outArray.length);
            response.setHeader("Expires", "0");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(outArray);
            outputStream.flush();
        } finally {
            byteArrayOutputStream.close();
            workbook.dispose();
            workbook.close();
        }
    }
}
