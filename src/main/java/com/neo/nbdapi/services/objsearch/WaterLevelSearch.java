package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelSearch  implements Serializable {
    @JsonProperty("s_id")
    private Long id;

    @JsonProperty("s_ts_id")
    private Long tsId;

    @JsonProperty("s_value")
    private Float value;

    @JsonProperty("s_status")
    private Integer status;

    @JsonProperty("s_manual")
    private Integer manual;

    @JsonProperty("s_warning")
    private Integer warning;

    @JsonProperty("s_create_user")
    private String createUser;

    @JsonProperty("s_station_id")
    private String stationId;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("s_hours")
    private Integer hours;
}
