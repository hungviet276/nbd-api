package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParameterChartMappingAndDataVM implements Serializable {
    private String stationCode;

    private String parameterTypeId;

    private String startDate;

    private String endDate;
}
