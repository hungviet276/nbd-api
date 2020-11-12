package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningManagerDetailDTO  implements Serializable {

    private  Long id;

    @NotNull(message = "Mã cảnh báo không được để trống")
    private Long warningThresholdId;

    private String createBy;


}
