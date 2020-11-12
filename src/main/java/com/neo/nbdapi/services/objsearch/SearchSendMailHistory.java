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
public class SearchSendMailHistory implements Serializable {
    @JsonProperty("s_stationId")
    String stationId;

    @JsonProperty("s_stationNo")
    String station_no;

    @JsonProperty("s_stationName")
    String station_name;

    @JsonProperty("s_warningId")
    String warningId;

    @JsonProperty("s_warning_code")
    String warningCode;

    @JsonProperty("s_warning_name")
    String warningName;

    @JsonProperty("s_status")
    String status;

    @JsonProperty("s_note")
    String note;

    @JsonProperty("s_fromdate")
    String fromDate;

    @JsonProperty("s_todate")
    String toDate;
}
