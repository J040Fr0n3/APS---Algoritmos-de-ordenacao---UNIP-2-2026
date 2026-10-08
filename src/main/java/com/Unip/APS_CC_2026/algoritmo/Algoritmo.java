package com.Unip.APS_CC_2026.algoritmo;

import java.util.Comparator;

import com.Unip.APS_CC_2026.model.Registro;

public class Algoritmo {
	
	public static class Estatisticas {
		public long comparacoes = 0;
		public long escritas = 0;
		public long trocas = 0;
		
		public long getTotalOperacoes() { return comparacoes + escritas; }
	}
	
	// ==========================================
	// SELECTION SORT
	// ==========================================
	public void selectionSort(Registro[] vetor, Comparator<Registro> comp, Estatisticas e) {
		int n = vetor.length;
		
		for (int i = 0; i < n - 1; i++) {
			int menor = i;
			
			for (int j = i + 1; j < n; j++) {
				e.comparacoes++;
				
				// Substitui: vetor[j] < vetor[menor]
				if (comp.compare(vetor[j], vetor[menor]) < 0) {
					menor = j;
				}
			}
			
			if (menor != i) {
				Registro temp = vetor[i];
				vetor[i] = vetor[menor];
				vetor[menor] = temp;
				
				e.escritas += 2;
				e.trocas++;
			}
		}
	}
	
	// ==========================================
	// INSERTION SORT
	// ==========================================
	public void insertionSort(Registro[] vetor, Comparator<Registro> comp, Estatisticas e) {
		for (int i = 1; i < vetor.length; i++) {
			Registro chave = vetor[i];
			int j = i - 1;
			
			while (j >= 0) {
				e.comparacoes++;
				
				// Substitui: vetor[j] > chave
				if (comp.compare(vetor[j], chave) > 0) {
					vetor[j + 1] = vetor[j];
					e.escritas++;
					j--;
				} else {
					break;
				}
			}
			
			vetor[j + 1] = chave;
			e.escritas++;
		}
	}
	
	// ==========================================
	// MERGE SORT
	// ==========================================
	public void mergeSort(Registro[] vetor, Comparator<Registro> comp, Estatisticas e) {
		Registro[] auxiliar = new Registro[vetor.length];
		mergeSort(vetor, auxiliar, 0, vetor.length - 1, comp, e);
	}
	
	private void mergeSort(Registro[] vetor, Registro[] auxiliar, int inicio, int fim, Comparator<Registro> comp, Estatisticas e) {
		if (inicio >= fim) { return; }
		
		int meio = inicio + (fim - inicio) / 2;
		
		mergeSort(vetor, auxiliar, inicio, meio, comp, e);
		mergeSort(vetor, auxiliar, meio + 1, fim, comp, e);
		
		int i = inicio;
		int j = meio + 1;
		int k = inicio;
		
		while (i <= meio && j <= fim) {
			e.comparacoes++;
			
			// Substitui: vetor[i] <= vetor[j]
			if (comp.compare(vetor[i], vetor[j]) <= 0) {
				auxiliar[k++] = vetor[i++];
			} else {
				auxiliar[k++] = vetor[j++];
			}
			
			e.escritas++;
		}
		
		while (i <= meio) {
			auxiliar[k++] = vetor[i++];
			e.escritas++;
		}
		
		while (j <= fim) {
			auxiliar[k++] = vetor[j++];
			e.escritas++;
		}
		
		for (int p = inicio; p <= fim; p++) {
			vetor[p] = auxiliar[p];
			e.escritas++;
		}
	}
	
	// ==========================================
	// QUICK SORT
	// ==========================================
	public void quickSort(Registro[] vetor, Comparator<Registro> comp, Estatisticas e) {
		quickSort(vetor, 0, vetor.length - 1, comp, e);
	}
	
	private void quickSort(Registro[] vetor, int inicio, int fim, Comparator<Registro> comp, Estatisticas e) {
		if (inicio < fim) {
			int pivo = particionar(vetor, inicio, fim, comp, e);
			
			quickSort(vetor, inicio, pivo - 1, comp, e);
			quickSort(vetor, pivo + 1, fim, comp, e);
		}
	}
	
	private int particionar(Registro[] vetor, int inicio, int fim, Comparator<Registro> comp, Estatisticas e) {
		Registro pivo = vetor[fim];
		int i = inicio - 1;
		
		for (int j = inicio; j < fim; j++) {
			e.comparacoes++;
			
			// Substitui: vetor[j] <= pivo
			if (comp.compare(vetor[j], pivo) <= 0) {
				i++;
				
				if (i != j) {
					trocar(vetor, i, j, e);
				}
			}
		}
		
		if (i + 1 != fim) {
			trocar(vetor, i + 1, fim, e);
		}
		
		return i + 1;
	}
	
	private void trocar(Registro[] vetor, int i, int j, Estatisticas e) {
		Registro temp = vetor[i];
		vetor[i] = vetor[j];
		vetor[j] = temp;
		
		e.escritas += 2;
		e.trocas++;
	}
}