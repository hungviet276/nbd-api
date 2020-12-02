package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Element {
    private Long id;
    private Long tsId;
    private Float value;
    private String timeStamp;
    private Integer status;
    private Integer manual;
    private Integer warning;
    private String user;
    private Long parentId;
    private Float avgValue;
    private Float minValue;
    private Float maxValue;
    private Float totalValue;
}
