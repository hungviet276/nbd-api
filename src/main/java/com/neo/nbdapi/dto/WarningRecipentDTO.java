package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningRecipentDTO {

    private Long id;

    private String stationId;

    private String stationName;

    private Long warningId;

    private String code;
}
