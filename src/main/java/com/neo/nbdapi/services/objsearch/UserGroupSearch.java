package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupSearch implements Serializable {
    @JsonProperty("s_Tram")
    private String stationName;

    @JsonProperty("s_TenNhom")
    private String groupName;

    @JsonProperty("s_QuanLy")
    private String groupParentName;

    @JsonProperty("s_Trangthai")
    private String status;
}
