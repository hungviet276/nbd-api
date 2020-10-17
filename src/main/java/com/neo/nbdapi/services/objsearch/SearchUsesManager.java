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
    @JsonProperty("s_id")
    String id;

    @JsonProperty("s_name")
    String name;

    @JsonProperty("s_mobile")
    String mobile;

    @JsonProperty("s_email")
    String email;

    @JsonProperty("s_gender")
    String gender;

    @JsonProperty("s_checkRole")
    String checkRole;

    @JsonProperty("s_cardNumber")
    String cardNumber;

    @JsonProperty("s_officeCode")
    String officeCode;


    @JsonProperty("s_statusId")
    String statusId;


    @JsonProperty("s_createDate")
    String createDate;

    @JsonProperty("s_createdBy")
    String createdBy;

    @JsonProperty("s_fromdate")
    String fromDate;

    @JsonProperty("s_todate")
    String toDate;

    @JsonProperty("s_datedwl")
    String datedwl;

}
