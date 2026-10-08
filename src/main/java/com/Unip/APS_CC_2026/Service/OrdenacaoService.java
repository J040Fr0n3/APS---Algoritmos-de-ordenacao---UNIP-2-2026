package com.Unip.APS_CC_2026.Service;

import com.Unip.APS_CC_2026.algoritmo.Algoritmo;
import com.Unip.APS_CC_2026.algoritmo.Algoritmo.Estatisticas;
import com.Unip.APS_CC_2026.dto.ResultadoBenchmark;
import com.Unip.APS_CC_2026.model.Registro;
import com.Unip.APS_CC_2026.model.RegistroComparatorFactory;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrdenacaoService {

    private final CsvReaderService csvReaderService;
    private final Algoritmo algoritmo = new Algoritmo();

    public OrdenacaoService(CsvReaderService csvReaderService) {
        this.csvReaderService = csvReaderService;
    }

    public Map<String, ResultadoBenchmark> executarBenchmark(String estado, List<String> filtros) {
        // 1. Carrega registros do estado escolhido
        List<Registro> listaOriginal = csvReaderService.carregarRegistrosPorEstado(estado);
        if (listaOriginal.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. Cria o Comparator dinâmico com a lista de filtros
        Comparator<Registro> comparator = RegistroComparatorFactory.criarComparator(filtros);

        Registro[] arrayBase = listaOriginal.toArray(new Registro[0]);
        Map<String, ResultadoBenchmark> resultados = new LinkedHashMap<>();

        // 3. Executa cada algoritmo em clones do vetor base
        resultados.put("QuickSort", rodarQuickSort(arrayBase.clone(), comparator));
        resultados.put("MergeSort", rodarMergeSort(arrayBase.clone(), comparator));
        resultados.put("InsertionSort", rodarInsertionSort(arrayBase.clone(), comparator));
        resultados.put("SelectionSort", rodarSelectionSort(arrayBase.clone(), comparator));

        return resultados;
    }

    private ResultadoBenchmark rodarQuickSort(Registro[] array, Comparator<Registro> comp) {
        Estatisticas e = new Estatisticas();
        long inicio = System.nanoTime();
        algoritmo.quickSort(array, comp, e);
        long fim = System.nanoTime();
        
        return new ResultadoBenchmark("Quick Sort", (fim - inicio) / 1_000_000.0, e, Arrays.asList(array));
    }

    private ResultadoBenchmark rodarMergeSort(Registro[] array, Comparator<Registro> comp) {
        Estatisticas e = new Estatisticas();
        long inicio = System.nanoTime();
        algoritmo.mergeSort(array, comp, e);
        long fim = System.nanoTime();
        
        return new ResultadoBenchmark("Merge Sort", (fim - inicio) / 1_000_000.0, e, Arrays.asList(array));
    }

    private ResultadoBenchmark rodarInsertionSort(Registro[] array, Comparator<Registro> comp) {
        Estatisticas e = new Estatisticas();
        long inicio = System.nanoTime();
        algoritmo.insertionSort(array, comp, e);
        long fim = System.nanoTime();
        
        return new ResultadoBenchmark("Insertion Sort", (fim - inicio) / 1_000_000.0, e, Arrays.asList(array));
    }

    private ResultadoBenchmark rodarSelectionSort(Registro[] array, Comparator<Registro> comp) {
        Estatisticas e = new Estatisticas();
        long inicio = System.nanoTime();
        algoritmo.selectionSort(array, comp, e);
        long fim = System.nanoTime();
        
        return new ResultadoBenchmark("Selection Sort", (fim - inicio) / 1_000_000.0, e, Arrays.asList(array));
    }
}