package com.neo.nbdapi.entity;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

public class TidalHarmonicConstants implements Serializable {

    private String stationId;

    private String waveName;

    private String amplitude;

    private String phase;

    private Date createDate;

    public TidalHarmonicConstants() {

    }

    public TidalHarmonicConstants(String stationId, String waveName, String amplitude, String phase, Date createDate) {
        this.stationId = stationId;
        this.waveName = waveName;
        this.amplitude = amplitude;
        this.phase = phase;
        this.createDate = createDate;
    }



    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getWaveName() {
        return waveName;
    }

    public void setWaveName(String waveName) {
        this.waveName = waveName;
    }


    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude = amplitude;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
