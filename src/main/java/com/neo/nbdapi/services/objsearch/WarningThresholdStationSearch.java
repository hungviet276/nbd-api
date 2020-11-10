package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarningThresholdStationSearch implements Serializable {
    @JsonProperty("s_id")
    private Long id;

    @JsonProperty("s_id_station")
    private String stationId;

    @JsonProperty("s_parameter_type_id")
    private Long parameterId;

    @JsonProperty("s_name_station")
    private String stationName;

    @JsonProperty("s_parameter_name")
    private String parameterName;

    @JsonProperty("s_value_level1")
    private Float valueLevel1;

    @JsonProperty("s_value_level2")
    private Float valueLevel2;

    @JsonProperty("s_value_level3")
    private Float valueLevel3;

    @JsonProperty("s_value_level4")
    private Float valueLevel4;

    @JsonProperty("s_value_level5")
    private Float valueLevel5;

}
