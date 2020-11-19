package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterChartMapping implements Serializable {

    // id cua bang
    private Long id;

    // id cua yeu to
    private Long parameterTypeId;

    // duong dan html bieu do
    private String templateDir;

    // ngay tao
    private Date createdDate;

    // nguoi tao
    private String createdBy;
}
