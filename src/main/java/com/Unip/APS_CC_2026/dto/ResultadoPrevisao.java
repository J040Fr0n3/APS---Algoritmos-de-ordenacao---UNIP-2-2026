package com.Unip.APS_CC_2026.dto;

import java.util.Map;

public class ResultadoPrevisao {
	
	private Map<String, Integer> historicoAgrupado;
	private double interceptoA;
	private double inclinacaoB;
	private String proximoPeriodo;
	private double previsaoProximoPeriodo;
	private String tendencia;
	
	public ResultadoPrevisao(Map<String, Integer> historicoAgrupado, double interceptoA, double inclinacaoB,
			String proximoPeriodo, double previsaoProximoPeriodo, String tendencia) {
		this.historicoAgrupado = historicoAgrupado;
		this.interceptoA = interceptoA;
		this.inclinacaoB = inclinacaoB;
		this.proximoPeriodo = proximoPeriodo;
		this.previsaoProximoPeriodo = previsaoProximoPeriodo;
		this.tendencia = tendencia;
	}

	public Map<String, Integer> getHistoricoAgrupado() {
		return historicoAgrupado;
	}

	public double getInterceptoA() {
		return interceptoA;
	}

	public double getInclinacaoB() {
		return inclinacaoB;
	}

	public String getProximoPeriodo() {
		return proximoPeriodo;
	}

	public double getPrevisaoProximoPeriodo() {
		return previsaoProximoPeriodo;
	}

	public String getTendencia() {
		return tendencia;
	}
	
}
