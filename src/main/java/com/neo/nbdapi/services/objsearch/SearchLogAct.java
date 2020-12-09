package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.nbdapi.anotation.validate.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchLogAct {

    @JsonProperty("s_menu_name")
    private String menuName;

    @JsonProperty("s_act")
    private String act;

    @JsonProperty("s_username")
    private String createdBy;

    @JsonProperty("s_from_date")
    @ValidDate(message = "Ngày bắt đầu không hợp lệ")
    private String fromDate;

    @JsonProperty("s_to_date")
    @ValidDate(message = "Ngày kết thúc không hợp lệ")
    private String toDate;
}
