package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchLogAct {


    @JsonProperty("s_menu_id")
    private String menuId;

    @JsonProperty("s_act")
    private String act;

    @JsonProperty("s_username")
    private String createdBy;

    @JsonProperty("s_from_date")
    private String fromDate;

    @JsonProperty("s_to_date")
    private String toDate;
}
