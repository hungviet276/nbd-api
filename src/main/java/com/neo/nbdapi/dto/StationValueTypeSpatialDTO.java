package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationValueTypeSpatialDTO  implements Serializable {

    private Long id;

    private Long stationId;

    private String stationCode;

    private String stationName;

    private Long valueTypeId;

    private String valueTypeCode;

    private String valueTypeName;

    private Integer variableSpatial;

    private String code;
}
