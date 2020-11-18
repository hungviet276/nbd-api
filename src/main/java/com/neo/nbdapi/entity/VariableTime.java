package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariableTime {

    private Float min;

    private Float max;

    private Float variableTime;

    private Float variableSpatial;

}
