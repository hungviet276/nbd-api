package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 12/8/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistoryDetail implements Serializable {
    private Long id;

    private Long notificationHistoryId;

    private Long parameterTypeId;

    private String parameterTypeName;

    private Float parameterValue;
}
