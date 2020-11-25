package com.neo.nbdapi.entity;

import java.io.Serializable;
import java.util.List;

public class DataResponse implements Serializable {

    private List<TidalHarmonicConstants> tidalHarmonicConstantesl;

    private String response;

    public DataResponse() {
    }

    public DataResponse(List<TidalHarmonicConstants> tidalHarmonicConstantesl, String response) {
        tidalHarmonicConstantesl = tidalHarmonicConstantesl;
        this.response = response;
    }

    public List<TidalHarmonicConstants> getTidalHarmonicConstantes() {
        return tidalHarmonicConstantesl;
    }

    public void setTidalHarmonicConstantes(List<TidalHarmonicConstants> tidalHarmonicConstantesl) {
        tidalHarmonicConstantesl = tidalHarmonicConstantesl;
    }



    public String getResponse() {
        return response;
    }



    public void setResponse(String response) {
        this.response = response;
}
}
