package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.NotificationToDayDTO;
import com.neo.nbdapi.dto.WarningManagerStationDTO;
import com.neo.nbdapi.services.WarningMangerStationService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

/**
 * @author thanglv on 11/26/2020
 * @project NBD
 */

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_NOTIFICATION)
public class NotificationController {

    @Autowired
    private WarningMangerStationService warningMangerStationService;

    @GetMapping("/get-notification-to-day")
    public List<NotificationToDayDTO> getNotificationToDay() throws SQLException {
        return warningMangerStationService.getListNotificationToday();
    }

    @GetMapping("/detail")
    public NotificationToDayDTO getNotificationToDayById(@RequestParam Long notificationHistoryId) throws SQLException {
        return warningMangerStationService.getNotificationById(notificationHistoryId);
    }
}
