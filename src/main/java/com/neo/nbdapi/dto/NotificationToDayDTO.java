package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    private Long stationId;

    private String stationName;
}
