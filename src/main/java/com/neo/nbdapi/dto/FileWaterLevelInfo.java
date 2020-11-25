package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class FileWaterLevelInfo implements Serializable {
    private String fileName;
    private Date modifyDate;
}
