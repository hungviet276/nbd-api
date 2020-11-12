package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchOutputsManger implements Serializable {
    @JsonProperty("s_station_type")
    String station_type_name;

    @JsonProperty("s_stationName")
    String station_name;

    @JsonProperty("s_stationNo")
    String station_no;

    @JsonProperty("s_valueType_id")
    String valueType_id;

    @JsonProperty("s_stations_no")
    String stations_no;

    @JsonProperty("s_stations")
    String stations_name;

    @JsonProperty("s_valuetype_id")
    String parameter_type_name;

    @JsonProperty("s_reponse")
    String reponse;

    @JsonProperty("s_area")
    String area;

    @JsonProperty("s_warning")
    String warning;

    @JsonProperty("s_user_create")
    String user_create;

    @JsonProperty("s_timereponse")
    String timereponse;

    @JsonProperty("s_createDate")
    String createDate;

    @JsonProperty("s_createdBy")
    String createdBy;

    @JsonProperty("s_fromdate")
    String fromDate;

    @JsonProperty("s_todate")
    String toDate;

    @JsonProperty("s_tableproductName")
    String tableproductName;

    @JsonProperty("s_createModify")
    String createModify;

    @JsonProperty("s_status")
    String status;

    @JsonProperty("s_station_id")
    String station_id;

    @JsonProperty("s_note")
    String note;

    @JsonProperty("s_userCreate")
    String userCreate;
}
