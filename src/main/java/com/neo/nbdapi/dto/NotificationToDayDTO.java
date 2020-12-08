package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.NotificationHistoryDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author thanglv on 11/26/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationToDayDTO implements Serializable {
    private Long id;

    private String code;

    private String name;

    private String description;

    private String content;

    private String rawTextContent;

    private String color;

    private String icon;

    private String createdAt;

    private String stationId;

    private String stationName;

    private String pushTimestamp;

    private List<NotificationHistoryDetail> details;
}
