package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUsesExpand implements Serializable {

    @JsonProperty("s_id")
    private String id;

    @JsonProperty("s_nameOutSite")
    private String name;

    @JsonProperty("s_phoneOutSite")
    private String phone;

    @JsonProperty("s_codeUserOutSite")
    private String code;

    @JsonProperty("s_emailOutSite")
    private String email;

    @JsonProperty("s_sexOutSite")
    private String sex;

    @JsonProperty("s_statusOutSite")
    private String status;

    @JsonProperty("s_idOutSite")
    private String cardId;

    @JsonProperty("s_positionOutSite")
    private String position;
}
