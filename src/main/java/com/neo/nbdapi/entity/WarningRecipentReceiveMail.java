package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningRecipentReceiveMail {

    private Long id;

    private String stationId;

    private String stationName;

    private Long warningManagerId;

    private  String warningManagerCode;
}
