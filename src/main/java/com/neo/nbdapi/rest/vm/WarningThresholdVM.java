package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningThresholdVM implements Serializable {
    @NotEmpty(message = "Id Yếu tố không được để trống")
//    @Size(max = 50, message = "Port dài tối đa 50 ký tự")
//    @Pattern(regexp = "^\\d+$", message = "Port phải là số")
    private Long idParameter;

    @NotEmpty(message = "Ngưỡng hủy cảnh báo không được để trống")
    private Long thresholdCancelID;

    @NotEmpty(message = "Ngưỡng cảnh báo không được để trống")
    private Long thresholdId;

    @NotEmpty(message = "Code cảnh báo không được để trống")
    private String warningThresholdCode;

    @NotEmpty(message = "Status không được để trống")
    private Integer status;

}
