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
public class SearchUsesManager implements Serializable {
    @JsonProperty("s_code")
    String code;

    @JsonProperty("s_username")
    String id;

    @JsonProperty("s_fromDate")
    Date fromDate;

    @JsonProperty("s_toDate")
    Date toDate;

}
