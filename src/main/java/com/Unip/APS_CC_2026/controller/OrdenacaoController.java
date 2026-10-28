package com.Unip.APS_CC_2026.controller;

import com.Unip.APS_CC_2026.Service.CsvReaderService;
import com.Unip.APS_CC_2026.Service.OrdenacaoService;
import com.Unip.APS_CC_2026.Service.PrevisaoService;
import com.Unip.APS_CC_2026.dto.ResultadoBenchmark;
import com.Unip.APS_CC_2026.dto.ResultadoPrevisao;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class OrdenacaoController {

    private final CsvReaderService csvReaderService;
    private final OrdenacaoService ordenacaoService;
    private final PrevisaoService previsaoService;

    public OrdenacaoController(CsvReaderService csvReaderService, OrdenacaoService ordenacaoService, PrevisaoService previsaoService) {
        this.csvReaderService = csvReaderService;
        this.ordenacaoService = ordenacaoService;
		this.previsaoService = previsaoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // Carrega estados disponíveis para o <select>
        List<String> estados = csvReaderService.listarEstadosDisponiveis();
        model.addAttribute("estados", estados);
        return "index";
    }

    @PostMapping("/ordenar")
    public String ordenar(
            @RequestParam("estado") String estado,
            @RequestParam(value = "filtros", required = false) List<String> filtros,
            Model model) {

        List<String> estados = csvReaderService.listarEstadosDisponiveis();
        model.addAttribute("estados", estados);
        model.addAttribute("estadoSelecionado", estado);
        model.addAttribute("filtrosSelecionados", filtros);

        if (filtros == null || filtros.isEmpty()) {
            model.addAttribute("erro", "Selecione pelo menos um critério/filtro de ordenação.");
            return "index";
        }

        // Executa a ordenação e obtém os resultados de todos os algoritmos
        Map<String, ResultadoBenchmark> resultados = ordenacaoService.executarBenchmark(estado, filtros);

        if (resultados.isEmpty()) {
            model.addAttribute("erro", "Nenhum registro encontrado para o estado selecionado (" + estado + ").");
            return "index";
        }

        model.addAttribute("resultados", resultados);

        // Pega a lista do QuickSort apenas para exibir a tabela de registros ordenados de amostra
        if (resultados.containsKey("QuickSort")) {
            model.addAttribute("registrosExemplo", resultados.get("QuickSort").getRegistrosOrdenados());
        }
        
        ResultadoPrevisao previsao = previsaoService.calculcarPrevisaoParaEstado(estado);
        if(previsao != null) {
        	model.addAttribute("previsao", previsao);
        } else {
        	model.addAttribute("avisoPrevisao", "Não há dados cronológicos suficientes para gerar uma previsão (minimo de 2 meses).");
        }
        
        return "index";
    }
    
    @GetMapping("/comparacao")
    public String comparacao(Model model) {
    	
    	List<String> estados = csvReaderService.listarEstadosDisponiveis();
    	model.addAttribute("estados", estados);
    	return "index";
    }
    
    @PostMapping("/comparar-estados")
    public String compararEstados(
    		@RequestParam("estado 1") String estado1,
    		@RequestParam("estado 2") String estado2,
    		@RequestParam(value = "filtros", required = false) List<String> filtros,
    		Model model) {
    	
    	List<String> estados = csvReaderService.listarEstadosDisponiveis();
    	model.addAttribute("estados", estados);
    	model.addAttribute("estado1Selecionado", estado1);
    	model.addAttribute("estado2Selecionado", estado2);
    	model.addAttribute("filtrosSelecionados", filtros);
    	
    	if(filtros == null || filtros.isEmpty()) {
    		
    		model.addAttribute("erro", "Selecione pelo menos um critério/filtro de ordenação.");
    		return "index";
    		
    	}
    	
    	if(estado1.equals(estado2)) {
    		model.addAttribute("erro", "Por favor, selecione dois estados diferentes.");
    		return "index";
    	}
    	
    	Map<String, ResultadoBenchmark> resultadosEstado1 = ordenacaoService.executarBenchmark(estado1, filtros);
    	Map<String, ResultadoBenchmark> resultadosEstado2 = ordenacaoService.executarBenchmark(estado2, filtros);

    	if(resultadosEstado1.isEmpty() || resultadosEstado2.isEmpty()) {
    		model.addAttribute("erro", "Dados insuficientes em um dos estados selecionados");
    		return "index";
    	}
    	
    	model.addAttribute("resultaodsEstado1", resultadosEstado1);
    	model.addAttribute("resultadosEstado2", resultadosEstado2);
    	
    	if(resultadosEstado1.containsKey("QuickSort")) {
    		model.addAttribute("registrosEstado1", resultadosEstado1.get("QuickSort").getRegistrosOrdenados());
    	}
    	if(resultadosEstado2.containsKey("QuickSort")) {
    		model.addAttribute("registrosEstado2", resultadosEstado2.get("QuickSort").getRegistrosOrdenados());
    	}
    	return "index";
    }
    
}