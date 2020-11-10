package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCDHHistory implements Serializable {
    @JsonProperty("s_stationNo")
    String station_no;

    @JsonProperty("s_stationName")
    String station_name;


    @JsonProperty("s_parameterName")
    String parameterName;

    @JsonProperty("s_createModify")
    String createModify;

    @JsonProperty("s_status")
    String status;

    @JsonProperty("s_note")
    String note;

    @JsonProperty("s_fromdate")
    String fromDate;

    @JsonProperty("s_todate")
    String toDate;

    @JsonProperty("s_station_id")
    String station_id;

    @JsonProperty("s_valueType_id")
    String valueType_id;

    @JsonProperty("s_userCreate")
    String userCreate;


}
