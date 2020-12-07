package com.neo.nbdapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private Date modifyDate;
}
