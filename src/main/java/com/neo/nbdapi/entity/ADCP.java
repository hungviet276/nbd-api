package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ADCP implements Serializable {
    private Long id;

    private String stationId;

    private String objectType;

    private String objectName;

    private String stationCode;

    private String stationName;

    private Long riverId;

    private String riverName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date timeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date timeEnd;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date timeAvg;

    private Long waterLevelStart;

    private Long waterLevelEnd;

    private Long waterLevelAvg;

    private Long speedAvg;

    private Long speedMax;

    private Long deepAvg;

    private Long deepMax;

    private Long squareRiver;

    private Long widthRiver;

    private Long waterFlow;

    private String note;

    private File fileUpload;

    private String linkFile;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
}
