package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author thanglv on 12/4/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningStationHistoryDTO {
    private Long notificationHistoryId;

    private String warningName;

    private String pushTimestamp;
}
