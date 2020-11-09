package com.neo.nbdapi.rest.vm;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningThresholdValueVM {
//    @Size(max = 50, message = "Port dài tối đa 50 ký tự")
//    @Pattern(regexp = "^\\d+$", message = "Port phải là số")
    @NotEmpty(message = "Id không được để trống")
    private Long id;

    @JsonProperty("station_add")
    @NotEmpty(message = "Trạm không được để trống")
    private String stationId;

    @NotEmpty(message = "Ngưỡng cảnh báo một không được để trống")
    private Float threshold1;

    @NotEmpty(message = "Ngưỡng cảnh báo hai không được để trống")
    private Float threshold2;

    @NotEmpty(message = "Ngưỡng cảnh báo ba không được để trống")
    private Float threshold3;

    @NotEmpty(message = "Ngưỡng cảnh bốn một không được để trống")
    private Float threshold4;

    @NotEmpty(message = "Ngưỡng cảnh báo năm không được để trống")
    private Float threshold5;

    @NotEmpty(message = "Không được để trống yếu tố")
    @JsonProperty("value_type_station")
    private Long parameterStation;

    @JsonProperty("dataThreshold")
    List<WarningThresholdVM> dataThreshold;

}
