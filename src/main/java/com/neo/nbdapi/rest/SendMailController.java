package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.entity.ComboBoxStr;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.SendMailHistoryService;
import com.neo.nbdapi.services.objsearch.SearchSendMailHistory;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_SEND_MAIL_HISTORY)
public class SendMailController {

    private Logger logger = LogManager.getLogger(SendMailController.class);

    @Autowired
    private SendMailHistoryService sendMailHistoryService;

    @PostMapping("/sendEmail")
    public DefaultResponseDTO sendEmail(
            @RequestParam("warningStationId") Long warningStationId
            , @RequestBody List<Long> groupEmailid
    ) throws MessagingException, SQLException {
        return sendMailHistoryService.sendEmail(groupEmailid, warningStationId);
    }

    @PostMapping("/get_list_outputs")
    public DefaultPaginationDTO getListOutpust(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return sendMailHistoryService.getListOutpust(defaultRequestPagingVM);
    }

    @GetMapping("/get_list_stations")
    public List<ComboBoxStr> get_list_group_users(@RequestParam("username") String userId) throws SQLException, BusinessException {
        return sendMailHistoryService.getListStations(userId);
    }

    @GetMapping("/getList_parameter_byStationId")
    public List<ComboBox> getListParameterByStations(@RequestParam("stationId") String stationId) throws SQLException, BusinessException {
        return sendMailHistoryService.getLstWarningManagerByStationId(stationId);
    }

    @PostMapping("/export")
    public void exportLogCDH(@RequestBody @Valid SearchSendMailHistory searchSendMailHistory, HttpServletResponse response) throws SQLException, IOException {
        SXSSFWorkbook workbook = sendMailHistoryService.export(searchSendMailHistory);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String fileName = "LOG_SEND_MAIL_" + "_" + DateUtils.getDateAndTimeFileName() + ".xlsx";
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
