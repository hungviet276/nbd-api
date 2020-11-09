package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.ManageCDHService;
import com.neo.nbdapi.services.ManageOutputService;
import com.neo.nbdapi.services.objsearch.SearchLogAct;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CDH_HISTORY)
public class CdhHistoryController {

    private Logger logger = LogManager.getLogger(CdhHistoryController.class);

    @Autowired
    private ManageCDHService manageCDHService;

    @Autowired
    private HikariDataSource ds;

    @PostMapping("/get_list_outputs")
    public DefaultPaginationDTO getListOutpust(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return manageCDHService.getListOutpust(defaultRequestPagingVM);
    }

    @GetMapping("/get_list_stations")
    public List<ComboBox>  get_list_group_users(@RequestParam("username") String userId) throws SQLException, BusinessException {
        return manageCDHService.getListStations(userId);
    }

    @GetMapping("/getList_parameter_byStationId")
    public List<ComboBox>  getListParameterByStations(@RequestParam("stationId") String stationId) throws SQLException, BusinessException {
        return manageCDHService.getListParameterByStations(stationId);
    }

//    @PostMapping("/export")
//    public void exportLogCDH(@RequestBody @Valid SearchLogAct searchLogAct, HttpServletResponse response) throws SQLException, IOException {
//        SXSSFWorkbook workbook = manageCDHService.export(searchLogAct);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        String fileName = Constants.LOG_ACT.FILE_NAME_EXPORT_LOG_ACT + "_" + DateUtils.getDateAndTimeFileName() + ".xlsx";
//        try {
//            workbook.write(byteArrayOutputStream);
//            byte[] outArray = byteArrayOutputStream.toByteArray();
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setContentLength(outArray.length);
//            response.setHeader("Expires", "0");
//            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//            OutputStream outputStream = response.getOutputStream();
//            outputStream.write(outArray);
//            outputStream.flush();
//        } finally {
//            byteArrayOutputStream.close();
//            workbook.dispose();
//            workbook.close();
//        }
//    }
}
