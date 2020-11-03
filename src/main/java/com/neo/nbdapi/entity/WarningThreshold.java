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
public class WarningThreshold implements Serializable {

    private String warningThresholdCode;

    private Long idParameter;

    private String nameParameter;

    private Long thresholdId;

    private Long thresholdCancelID;

    private Integer status;
}
