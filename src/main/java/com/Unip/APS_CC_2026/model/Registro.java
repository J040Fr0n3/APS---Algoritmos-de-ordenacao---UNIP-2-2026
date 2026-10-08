package com.Unip.APS_CC_2026.model;

import java.time.LocalDateTime;

public class Registro {
    private String idBdq;
    private String focoId;
    private double lat;
    private double lon;
    private LocalDateTime dataPas;
    private String pais;
    private String estado;
    private String municipio;
    private String bioma;

    public Registro(String idBdq, String focoId, double lat, double lon, LocalDateTime dataPas,
                    String pais, String estado, String municipio, String bioma) {
        this.idBdq = idBdq;
        this.focoId = focoId;
        this.lat = lat;
        this.lon = lon;
        this.dataPas = dataPas;
        this.pais = pais;
        this.estado = estado;
        this.municipio = municipio;
        this.bioma = bioma;
    }

    // Getters
    public String getIdBdq() { return idBdq; }
    public String getFocoId() { return focoId; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public LocalDateTime getDataPas() { return dataPas; }
    public String getPais() { return pais; }
    public String getEstado() { return estado; }
    public String getMunicipio() { return municipio; }
    public String getBioma() { return bioma; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s", estado, municipio, bioma, dataPas);
    }
}
