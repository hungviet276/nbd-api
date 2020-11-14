package com.neo.nbdapi.rest.vm;

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
public class WaterLevelVM implements Serializable {

    private Long id;

    private Long tsId;

    private Integer value;

    private Integer status;

    private Integer manual;

    private Integer warning;

    private String timestamp;

    private String stationId;

    private String user;
}
