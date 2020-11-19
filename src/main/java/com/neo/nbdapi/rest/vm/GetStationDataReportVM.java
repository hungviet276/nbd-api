package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author thanglv on 11/18/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStationDataReportVM implements Serializable {
    private String stationCode;
}
