package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterLevel implements Serializable {

    private Long id;

    private Long tsId;

    private Float value;

    private String timestamp;

    private Integer status;

    private Integer manual;

    private Integer warning;

    private String createUser;
}
