package com.acpagro.tareopalta.modelo;

import java.util.ArrayList;

public class CopiaTareo {
    private String IDTAREO_ORIGEN, IDTAREO_NUEVO;
    public static ArrayList<CopiaTareo> listaCopia = new ArrayList<>();

    public CopiaTareo(String IDTAREO_ORIGEN, String IDTAREO_NUEVO) {
        this.IDTAREO_ORIGEN = IDTAREO_ORIGEN;
        this.IDTAREO_NUEVO = IDTAREO_NUEVO;
    }

    public CopiaTareo() {
    }

    public String getIDTAREO_ORIGEN() {
        return IDTAREO_ORIGEN;
    }

    public void setIDTAREO_ORIGEN(String IDTAREO_ORIGEN) {
        this.IDTAREO_ORIGEN = IDTAREO_ORIGEN;
    }

    public String getIDTAREO_NUEVO() {
        return IDTAREO_NUEVO;
    }

    public void setIDTAREO_NUEVO(String IDTAREO_NUEVO) {
        this.IDTAREO_NUEVO = IDTAREO_NUEVO;
    }
}
