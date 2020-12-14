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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm",timezone = "Asia/Ho_Chi_Minh")
    private Date timeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm" ,timezone = "Asia/Ho_Chi_Minh")
    private Date timeEnd;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm",timezone = "Asia/Ho_Chi_Minh")
    private Date timeAvg;

    private Float waterLevelStart;

    private Float waterLevelEnd;

    private Float waterLevelAvg;

    private Float speedAvg;

    private Float speedMax;

    private Float deepAvg;

    private Float deepMax;

    private Float squareRiver;

    private Float widthRiver;

    private Float waterFlow;

    private String note;

    private String data;

    private String dataAvg;

    private Float totalTurb;

    private File fileUpload;

    private String linkFile;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;

    private String dataTotalDeep;

    private String dataDistance;

    private Float suspendedMaterial;

    private Integer measureNth;
}
