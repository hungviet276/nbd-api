package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 11/25/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterDisplayChartDTO implements Serializable {
    private String parameterTypeId;

    private String type;

    private String amountDate;

    private String startDate;

    private String endDate;
}
