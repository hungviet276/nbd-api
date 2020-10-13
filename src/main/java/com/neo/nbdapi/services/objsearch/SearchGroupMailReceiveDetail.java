package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchGroupMailReceiveDetail {

    @JsonProperty("s_id_detail")
    private Long id;

    @JsonProperty("s_id_group")
    private Long idGroup;

    @JsonProperty("s_group_name_detail")
    private String groupName;

    @JsonProperty("s_name_detail")
    private String name;
}
