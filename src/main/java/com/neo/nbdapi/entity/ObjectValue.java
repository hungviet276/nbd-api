package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author thanglv on 10/16/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ObjectValue implements Serializable {
//    @JsonProperty("ts_id")
    private Long tsId;

//    @JsonProperty("value")
    private Float value;

//    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private Date timestamp;

//    @JsonProperty("status")
    private Integer status;

//    @JsonProperty("manual")
    private Integer manual;

//    @JsonProperty("warning")
    private Integer warning;

//    @JsonProperty("create_user")
    private String createUser;

    private Float minValue;

    private Float maxValue;

    private Float avgValue;

    private Float totalValue;

    private String tblName;
}
