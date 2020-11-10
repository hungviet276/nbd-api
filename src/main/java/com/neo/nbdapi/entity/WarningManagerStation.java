package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningManagerStation  implements Serializable {

    private Long id;

    private String stationId;

    private String stationName;

    private String warningCode;

    private String warningName;

    private String icon;

    private Date createDate;

    private String description;

    private String content;

    private String color;
}
