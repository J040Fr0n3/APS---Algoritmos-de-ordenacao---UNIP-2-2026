package com.Unip.APS_CC_2026.dto;

import com.Unip.APS_CC_2026.algoritmo.Algoritmo.Estatisticas;
import com.Unip.APS_CC_2026.model.Registro;
import java.util.List;

public class ResultadoBenchmark {
    private String algoritmoNome;
    private double tempoMs;
    private Estatisticas estatisticas;
    private List<Registro> registrosOrdenados;

    public ResultadoBenchmark(String algoritmoNome, double tempoMs, Estatisticas estatisticas, List<Registro> registrosOrdenados) {
        this.algoritmoNome = algoritmoNome;
        this.tempoMs = tempoMs;
        this.estatisticas = estatisticas;
        this.registrosOrdenados = registrosOrdenados;
    }

    // Getters
    public String getAlgoritmoNome() { return algoritmoNome; }
    public double getTempoMs() { return tempoMs; }
    public Estatisticas getEstatisticas() { return estatisticas; }
    public List<Registro> getRegistrosOrdenados() { return registrosOrdenados; }
}