package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarningManagerStationSearch implements Serializable {

    @JsonProperty("s_id")
    private Long id;

    @JsonProperty("s_id_station")
    private String stationId;

    @JsonProperty("s_name_station")
    private String stationName;

    @JsonProperty("s_code_warning")
    private String warningCode;

    @JsonProperty("s_name_warning")
    private String warningName;

    @JsonProperty("s_icon")
    private String icon;

    @JsonProperty("s_start_date")
    private String startDate;

    @JsonProperty("s_end_date")
    private String endDate;
}
