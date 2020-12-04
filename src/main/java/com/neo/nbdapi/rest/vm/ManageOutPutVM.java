package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManageOutPutVM {
    private String prodTableName;
    private String stationId;
    private String parameterTypeId;
    private String pustTimeOld;
    private String prodId;
    private String value;
    private String userLogin;
}
