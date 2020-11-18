package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.ObjectValue;
import com.neo.nbdapi.entity.ParameterChartMapping;
import com.neo.nbdapi.entity.StationTimeSeries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author thanglv on 11/18/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSeriesDataDTO implements Serializable {
    // thông tin để vẽ biểu đồ
    private ParameterChartMapping parameterChartMapping;
    // thông tin yếu tố của trạm
    private StationTimeSeries stationTimeSeries;
    // dữ liệu đo của yếu tố 7 ngày trước đến bây giờ
    private List<ObjectValue> data;
}
