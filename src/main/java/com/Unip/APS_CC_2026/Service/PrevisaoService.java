package com.Unip.APS_CC_2026.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Unip.APS_CC_2026.algoritmo.Algoritmo;
import com.Unip.APS_CC_2026.dto.ResultadoPrevisao;
import com.Unip.APS_CC_2026.model.Registro;
import com.Unip.APS_CC_2026.model.RegistroComparatorFactory;

@Service
public class PrevisaoService {
	private final CsvReaderService csvReaderService;
	private final Algoritmo algoritmo = new Algoritmo();
	
	public PrevisaoService(CsvReaderService csvReaderService) {
		this.csvReaderService = csvReaderService;
	}
	
	public ResultadoPrevisao calculcarPrevisaoParaEstado(String estado) {
		List<Registro> listaOriginal = csvReaderService.carregarRegistrosPorEstado(estado);
		if(listaOriginal == null || listaOriginal.isEmpty()) {return null;}
		
		Registro[] arrayData = listaOriginal.toArray(new Registro[0]);
		Comparator<Registro> copmData = RegistroComparatorFactory.criarComparator(List.of("data_pas"));
		
		algoritmo.quickSort(arrayData, copmData, new Algoritmo.Estatisticas());
		List<Registro> registrosOrdenadosPorData = Arrays.asList(arrayData);
		
		Map<YearMonth, Integer> agrupamentoPorMes = registrosOrdenadosPorData.stream()
				.filter(r -> r.getDataPas() != null)
				.collect(Collectors.groupingBy(
						r -> YearMonth.from(r.getDataPas()),
						TreeMap::new,
						Collectors.summingInt(r -> 1)
						));
		if(agrupamentoPorMes.size()<2) {return null;}
		
		int n = agrupamentoPorMes.size();
		double somaX = 0;
		double somaY = 0;
		double somaXY = 0;
		double somaX2 = 0;
		int x = 1;
		
		Map<String, Integer> historicoStr = new LinkedHashMap<>();
		YearMonth ultimoMesRegistrado = null;
		
		for (Map.Entry<YearMonth, Integer> entry : agrupamentoPorMes.entrySet()) {
			int y = entry.getValue();
			historicoStr.put(entry.getKey().toString(), y);
			ultimoMesRegistrado = entry.getKey();
			
			somaX += x;
			somaY += y;
			somaXY += (x*y);
			somaX2 += (x*x);
			x++;
		}
		
		double denominadorB = (n * somaX2) - (somaX * somaX);
		double b = 0;
		
		if(denominadorB != 0) {b = ((n*somaXY) - (somaX*somaY)) / denominadorB;}
		
		double a = (somaY - (b*somaX)) / n;
		
		int proximoX = n+1;
		double previsaoY = a + (b*proximoX);
		if(previsaoY < 0)previsaoY = 0;
		
		String proximoPeriodo = ultimoMesRegistrado != null
				? ultimoMesRegistrado.plusMonths(1).toString()
				: "Prózimo MÊs";
		
		String tendenciaStr;
		if(b > 0) {
			tendenciaStr = "Crescente";
		} else if(b < 0) {
			tendenciaStr = "Decrescente";
		} else {
			tendenciaStr = "Estável";
		}
		
		return new ResultadoPrevisao(historicoStr, a, b, proximoPeriodo, previsaoY, tendenciaStr);
		
	}
	
}
