package com.neo.nbdapi.dto;

import java.io.Serializable;

public class WaterLevelDTO implements Serializable {

    private String comandExcute;

    private Long tsId;

    String fileName;

    public WaterLevelDTO() {

    }

    public WaterLevelDTO(String comandExcute, Long tsId, String fileName) {

        this.comandExcute = comandExcute;
        this.tsId = tsId;
        this.fileName = fileName;
        

    }

    public String getComandExcute() {
        return comandExcute;
    }


    public void setComandExcute(String comandExcute) {
        this.comandExcute = comandExcute;
    }


    public Long getTsId() {
        return tsId;
    }


    public void setTsId(Long tsId) {
        this.tsId = tsId;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
