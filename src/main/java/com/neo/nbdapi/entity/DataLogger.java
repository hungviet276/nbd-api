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

public class DataLogger implements Serializable {
    private Long dataLoggerId;
    private String dataLoggerCode;
    private String modem;
    private Integer port;
    private String parameterName;
}