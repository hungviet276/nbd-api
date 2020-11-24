package com.neo.nbdapi.entity;

import java.io.Serializable;
import java.util.Date;

public class TidalHarmonicConstants implements Serializable {
    private Long TsId;

    private String waveName;

    private Float amplitude;

    private Float phase;

    private Date createDate;

    public TidalHarmonicConstants() {
    }

    public TidalHarmonicConstants(Long tsId, String waveName, Float amplitude, Float phase, Date createDate) {
        TsId = tsId;
        this.waveName = waveName;
        this.amplitude = amplitude;
        this.phase = phase;
        this.createDate = createDate;
    }

    public Long getTsId() {
        return TsId;
    }

    public void setTsId(Long tsId) {
        TsId = tsId;
    }

    public String getWaveName() {
        return waveName;
    }

    public void setWaveName(String waveName) {
        this.waveName = waveName;
    }

    public Float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(Float amplitude) {
        this.amplitude = amplitude;
    }

    public Float getPhase() {
        return phase;
    }

    public void setPhase(Float phase) {
        this.phase = phase;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
