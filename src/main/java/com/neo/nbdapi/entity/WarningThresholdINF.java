package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningThresholdINF implements Serializable {

    private Integer warningThreshold;

    private Integer warningThresholdCancel;

    private Float valueLevel1;

    private Float valueLevel2;

    private Float valueLevel3;

    private Float valueLevel4;

    private Float valueLevel5;
}
