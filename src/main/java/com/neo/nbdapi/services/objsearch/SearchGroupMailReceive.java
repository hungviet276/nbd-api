package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchGroupMailReceive implements Serializable {
        @JsonProperty("s_id")
        String id;

        @JsonProperty("s_code")
        String code;

        @JsonProperty("s_name")
        String name;

        @JsonProperty("s_status")
        String status;

        @JsonProperty("s_description")
        String description;
}
