package com.neo.nbdapi.rest.vm;

import com.neo.nbdapi.anotation.validate.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author thanglv on 12/4/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarningStationHistoryVM implements Serializable {

    @NotEmpty(message = "Mã trạm không được trống")
    private String stationId;

    @NotEmpty(message = "Ngày bắt đầu không được trống")
    @ValidDate(format = "dd/MM/yyyy", message = "Ngày bắt đầu không hợp lệ : dd/mm/yyyy")
    private String fromDate;

    @ValidDate(format = "dd/MM/yyyy", message = "Ngày kết thúc không hợp lệ : dd/mm/yyyy")
    private String toDate;
}
