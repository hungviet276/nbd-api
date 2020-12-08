package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningMangerDetailInfoDTO {

    private Long id;

    private Long idParameter;

    private Long idWarningThreshold;

    private String nameParameter;

    private String warningThresholdCode;

    private Integer warningThreshold;

    private Integer warningThresholdCancel;

    private Float valueLevel1;

    private Float valueLevel2;

    private Float valueLevel3;

    private Float valueLevel4;

    private Float valueLevel5;

    private  String createBy;

    private String createAt;

}
