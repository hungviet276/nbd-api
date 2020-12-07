package com.neo.nbdapi.services.objsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 12/4/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchNotificationHistory implements Serializable {
    private String stationId;

    private String fromDate;

    private String toDate;
}
