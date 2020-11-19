package com.neo.nbdapi.rest.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.neo.nbdapi.anotation.validate.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetParameterChartMappingAndDataVM implements Serializable {

    @NotEmpty(message = "stationCode không được trống")
    @Size(max = 50, message = "stationCode không vượt quá 50 ký tự")
    private String stationCode;

    @NotEmpty(message = "parameterTypeId không được trống")
    @Size(max = 20, message = "parameterTypeId không được quá 20 ký tự")
    private String parameterTypeId;

    @Size(max = 20, message = "Type không vượt quá 20 ký tự")
    private String type;

    @NotEmpty(message = "Ngày bắt đầu không được trống")
    @ValidDate(format = "dd/mm/yyyy HH:mm", message = "Ngày bắt đầu không hợp lệ")
    private String startDate;

    @ValidDate(format = "dd/mm/yyyy HH:mm", message = "Ngày kết thúc không hợp lệ")
    private String endDate;
}
