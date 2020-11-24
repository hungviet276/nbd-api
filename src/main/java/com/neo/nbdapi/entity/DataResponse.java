package com.neo.nbdapi.entity;

import java.io.Serializable;
import java.util.List;

public class DataResponse implements Serializable {

    private List<TidalHarmonicConstants> TidalHarmonicConstantes;

    private String response;

    public DataResponse() {
    }

    public DataResponse(List<TidalHarmonicConstants> tidalHarmonicConstantesl, String response) {
        TidalHarmonicConstantes = tidalHarmonicConstantesl;
        this.response = response;
    }

    public List<TidalHarmonicConstants> getTidalHarmonicConstantes() {
        return TidalHarmonicConstantes;
    }

    public void setTidalHarmonicConstantes(List<TidalHarmonicConstants> tidalHarmonicConstantes) {
        TidalHarmonicConstantes = tidalHarmonicConstantes;
    }



    public String getResponse() {
        return response;
    }



    public void setResponse(String response) {
        this.response = response;
}
}
