package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterLevelVM implements Serializable {

    private String idStation;

    private Long idWaterLevel;

    private Integer valueWaterLevel;

    private String user;

}
