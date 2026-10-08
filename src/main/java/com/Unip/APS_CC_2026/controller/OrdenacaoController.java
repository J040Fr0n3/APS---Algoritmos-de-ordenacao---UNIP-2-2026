package com.Unip.APS_CC_2026.controller;

import com.Unip.APS_CC_2026.Service.CsvReaderService;
import com.Unip.APS_CC_2026.Service.OrdenacaoService;
import com.Unip.APS_CC_2026.dto.ResultadoBenchmark;

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

    public OrdenacaoController(CsvReaderService csvReaderService, OrdenacaoService ordenacaoService) {
        this.csvReaderService = csvReaderService;
        this.ordenacaoService = ordenacaoService;
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

        return "index";
    }
}