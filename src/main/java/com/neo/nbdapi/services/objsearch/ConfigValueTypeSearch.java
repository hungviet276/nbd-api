package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigValueTypeSearch  implements Serializable {

    @JsonProperty("s_id")
    private Long id;

    @JsonProperty("s_station_id")
    private String stationId;

    @JsonProperty("s_value_type_id")
    private Long valueTypeId;

    @JsonProperty("s_station_name")
    private String stationName;

    @JsonProperty("s_value_type_name")
    private String valueTypename;

    @JsonProperty("s_min")
    private Float min;

    @JsonProperty("s_max")
    private Float max;

    @JsonProperty("s_variable_time")
    private Float variableTime;

    @JsonProperty("s_variable_spatial")
    private Float variableSpatial;

    @JsonProperty("s_start_apply_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private String startDate;

    @JsonProperty("s_end_apply_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private String endDate;

    @JsonProperty("s_code")
    private String code;
}
