package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelExecutedVM {

    private String stationId;

    private String startDate;

    private String endDate;
}
