package com.Unip.APS_CC_2026.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Unip.APS_CC_2026.Service.CsvReaderService;
import com.Unip.APS_CC_2026.Service.OrdenacaoService;
import com.Unip.APS_CC_2026.Service.PrevisaoService;
import com.Unip.APS_CC_2026.dto.ResultadoBenchmark;
import com.Unip.APS_CC_2026.dto.ResultadoPrevisao;

@RestController
public class ApiController {
	
	private final CsvReaderService csvReaderService;
	private final OrdenacaoService ordenacaoService;
	private final PrevisaoService previsaoService;
	
	public ApiController(CsvReaderService csvReaderService, OrdenacaoService ordenacaoService,
			PrevisaoService previsaoService) {
		this.csvReaderService = csvReaderService;
		this.ordenacaoService = ordenacaoService;
		this.previsaoService = previsaoService;
	}
	
	
	@GetMapping("/api/estados")
	public ResponseEntity<List<String>> listarEstados(){
		return ResponseEntity.ok(csvReaderService.listarEstadosDisponiveis());
	}
	
	@PostMapping("/api/ordenar")
	public ResponseEntity<?> ordenar(
			@RequestParam("estado") String estado,
			@RequestParam(value = "filtros", required = false) List<String> filtros){
		
		if(filtros == null || filtros.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("erro", "Selecione pelo menos um críterio/filtro de ordenação."));
		}
		
		Map<String, ResultadoBenchmark> resultados = ordenacaoService.executarBenchmark(estado, filtros);
		
		if(resultados.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("erro", "Nenhum registro encontrado para o estado selecionado ("+ estado +")"));
		}
		
		Map<String, Object> resposta = new HashMap<>();
		resposta.put("estadoSelecionado", estado);
		resposta.put("filtrosSelecionados", filtros);
		resposta.put("resultados", resultados);
		
		if(resultados.containsKey("QuickSort")) {
			resposta.put("registrosExemplo", resultados.get("QuickSort").getRegistrosOrdenados());
		}
		
		ResultadoPrevisao previsao = previsaoService.calculcarPrevisaoParaEstado(estado);
		if(previsao != null) {
			resposta.put("previsao", previsao);
		} else {
			resposta.put("avisoPrevisao", "Não há dados cronológicos suficientes para gerar uma previsão (minimo de 2 meses).");
		}
		return ResponseEntity.ok(resposta);
	}
	
	@PostMapping("/api/comparar-estados")
	public ResponseEntity<?> compararEstados(
			@RequestParam("estado1") String estado1,
			@RequestParam("estado2") String estado2,
			@RequestParam(value = "filtros", required = false) List<String> filtros){
		
		if(filtros == null || filtros.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("erro", "Selecione pelo menos um critério/filtro de ordenação"));
		}
		
		if (estado1.equals(estado2)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Por favor, selecione dois estados diferentes."));
        }
        
        Map<String, ResultadoBenchmark> resultadosEstado1 = ordenacaoService.executarBenchmark(estado1, filtros);
        Map<String, ResultadoBenchmark> resultadosEstado2 = ordenacaoService.executarBenchmark(estado2, filtros);

        if (resultadosEstado1.isEmpty() || resultadosEstado2.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Dados insuficientes em um dos estados selecionados."));
        }
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("estado1Selecionado", estado1);
        resposta.put("estado2Selecionado", estado2);
        resposta.put("filtrosSelecionados", filtros);
        resposta.put("resultadosEstado1", resultadosEstado1);
        resposta.put("resultadosEstado2", resultadosEstado2);
        
        if (resultadosEstado1.containsKey("QuickSort")) {
            resposta.put("registrosEstado1", resultadosEstado1.get("QuickSort").getRegistrosOrdenados());
        }
        if (resultadosEstado2.containsKey("QuickSort")) {
            resposta.put("registrosEstado2", resultadosEstado2.get("QuickSort").getRegistrosOrdenados());
        }
        
        ResultadoPrevisao previsaoEstado1 = previsaoService.calculcarPrevisaoParaEstado(estado1);
        if (previsaoEstado1 != null) {
            resposta.put("previsaoEstado1", previsaoEstado1);
        }
        
        ResultadoPrevisao previsaoEstado2 = previsaoService.calculcarPrevisaoParaEstado(estado2);
        if (previsaoEstado2 != null) {
            resposta.put("previsaoEstado2", previsaoEstado2);
        }
        
        return ResponseEntity.ok(resposta);
		
	}
	
}
