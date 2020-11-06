package com.neo.nbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningManagerStationDTO implements Serializable {

    private Long id;

    @JsonProperty("codeWarning")
    private String code;

    @JsonProperty("nameWarning")
    private String name;

    @JsonProperty("descriptionWarning")
    private String description;

    @JsonProperty("contentWarning")
    private String content;

    @JsonProperty("colorWarning")
    private String color;

    @JsonProperty("iconWarning")
    private String icon;

    @JsonProperty("stationWarning")
    private Long stationId;

    @JsonProperty("createBy")
    private String createBy;

    private List<WarningManagerDetailDTO> dataWarning;

}
