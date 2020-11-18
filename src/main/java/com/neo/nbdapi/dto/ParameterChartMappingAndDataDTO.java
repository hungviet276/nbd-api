package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.ParameterChartMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterChartMappingAndDataDTO implements Serializable {
    private ParameterChartMapping chartMapping;

    private Object data;
}
