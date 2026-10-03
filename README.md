# 🔥 Análise e Ordenação de Dados de Queimadas no Brasil (2023–2024)

> Trabalho acadêmico desenvolvido para a **Universidade Paulista (UNIP)** com o objetivo de comparar a eficiência de algoritmos de ordenação no processamento de grandes volumes de dados ambientais.

---

## 📌 Sobre o Projeto

Este sistema foi desenvolvido para importar, processar e ordenar uma base de dados expressiva com **mais de 5 milhões de registros** de focos de queimadas ocorridos em todo o território nacional entre 2023 e 2024.

O foco principal da aplicação é analisar empiricamente o desempenho (tempo de execução e consumo de recursos) de **quatro algoritmos clássicos de ordenação**:

* **Insertion Sort**
* **Selection Sort**
* **Merge Sort**
* **Quick Sort**

A aplicação conta com uma interface web para visualização dos resultados, filtros e métricas de desempenho comparativas.

---

## 🛠️ Tecnologias Utilizadas

* **Java** (JDK 17+)
* **Spring Boot**
  * *Spring Boot DevTools* (Agilidade no desenvolvimento)
  * *Spring Web* (Criação de Web Services / APIs REST)
* **Thymeleaf** (Renderização do Front-end do lado do servidor)
* **HTML5 / CSS3 / JavaScript** (Interface web)

---

## 📊 Algoritmos em Comparação

| Algoritmo | Complexidade (Melhor Caso) | Complexidade (Médio Caso) | Complexidade (Pior Caso) |
|---|---|---|---|
| **Insertion Sort** | $\mathcal{O}(n)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ |
| **Selection Sort** | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ | $\mathcal{O}(n^2)$ |
| **Merge Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ |
| **Quick Sort** | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n^2)$ |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

* **Java 17** ou superior instalado
* **Maven** instalado (ou utilizar o wrapper `./mvnw`)
* A base de dados CSV baixada e configurada na pasta indicada no projeto.

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/J040Fr0n3/APS---Algoritmos-de-ordenacao---UNIP-2-2026.git](https://github.com/J040Fr0n3/APS---Algoritmos-de-ordenacao---UNIP-2-2026.git)
   cd APS---Algoritmos-de-ordenacao---UNIP-2-2026
