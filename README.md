# 🔥 Análise e Ordenação de Dados de Queimadas no Brasil (2023–2024)

> APS de Estrutura de Dados – Ciências da Computação – **UNIP**, 2º semestre de 2026.
> Aplicação web que compara o desempenho de quatro algoritmos de ordenação sobre dados reais de focos de queimadas do INPE.

## 👥 Integrantes

- Gabriel Vieira de Medeiros
- Hiago Muller de Morais
- João Pedro Pereira Frone
- Marcelo Felipe dos Santos Agante

---

## 📌 Sobre o projeto

O sistema carrega os focos de queimadas detectados por satélite (BDQueimadas/INPE) de **todas as 27 UFs, anos de 2023 e 2024**, ordena os registros de um estado com **Insertion Sort, Selection Sort, Merge Sort e Quick Sort** (implementados do zero, sem `Arrays.sort`) e mostra, para cada algoritmo:

- tempo de execução em milissegundos (`System.nanoTime()`, medido no backend, sem latência de rede);
- número de **comparações**, **escritas** (movimentações) e **trocas**.

Além da comparação entre algoritmos, o sistema oferece:

- **Mapa do Brasil em SVG** para escolher o estado com um clique;
- **Comparador de estados**: executa o benchmark para dois estados lado a lado;
- **Previsão de queimadas**: agrupa os focos por mês e aplica **regressão linear simples (mínimos quadrados)** para estimar o próximo mês, informando a tendência (crescente, decrescente ou estável);
- Gráficos (Chart.js) do histórico mensal e da previsão.

### Base de dados

| Item | Valor |
|---|---|
| Fonte | [INPE – BDQueimadas](https://terrabrasilis.dpi.inpe.br/queimadas/bdqueimadas/) |
| Período | 2023 e 2024 |
| Arquivos | 54 CSVs (1 por UF por ano) em `src/main/resources/data/<UF>/` |
| Total | ≈ 468 mil registros |
| Maior estado | Pará, 97.785 registros |

Colunas utilizadas: `id_bdq, foco_id, lat, lon, data_pas, pais, estado, municipio, bioma`.
Linhas malformadas (campos faltando ou valores inválidos) são ignoradas e registradas no console.

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 4.1.1** (Spring Web MVC, Thymeleaf, DevTools)
- **Maven** (wrapper incluso)
- **HTML5 / CSS / JavaScript (ES6+)**, **Tailwind CSS** e **Chart.js** (ambos via CDN)

---

## 🚀 Como executar

### Pré-requisitos

- Java 21 (ou superior) instalado
- Acesso à internet no navegador (Tailwind e Chart.js são carregados por CDN)
- Não é preciso instalar o Maven: use o wrapper `mvnw`
- Os CSVs já estão no repositório; não há configuração adicional

### Passo a passo

```bash
git clone https://github.com/J040Fr0n3/APS---Algoritmos-de-ordenacao---UNIP-2-2026.git
cd APS---Algoritmos-de-ordenacao---UNIP-2-2026

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Depois acesse **http://localhost:8080**.

### Como usar

1. Na aba **Ordenador**, clique em um estado no mapa.
2. Marque um ou mais critérios de ordenação (Município, Data, Bioma). Quando há mais de um, eles são encadeados na ordem de seleção (o seguinte desempata o anterior).
3. Execute e veja os cards de cada algoritmo, a amostra dos registros ordenados e o gráfico de previsão.
4. Na aba **Comparador**, escolha dois estados diferentes para comparar benchmarks e previsões.

> ⚠️ **Atenção ao tamanho do estado.** Cada requisição executa os **quatro** algoritmos. Insertion e Selection são O(n²): em estados grandes (ex.: PA, MT) a resposta pode levar dezenas de segundos.

---

## 🧱 Arquitetura

```
src/main/java/com/Unip/APS_CC_2026/
├── algoritmo/   Algoritmo.java              → os 4 algoritmos + contadores (Estatisticas)
├── controller/  ApiController.java          → endpoints REST
│                ThymeleafController.java    → serve apenas o index.html
├── Service/     CsvReaderService.java       → leitura dos CSVs por UF
│                OrdenacaoService.java       → executa e cronometra os algoritmos
│                PrevisaoService.java        → agrupamento mensal + regressão linear
├── model/       Registro.java               → entidade de um foco de queimada
│                RegistroComparatorFactory   → monta o Comparator a partir dos critérios
└── dto/         ResultadoBenchmark, ResultadoPrevisao
src/main/resources/
├── data/<UF>/*.csv   templates/index.html   static/js/script.js   static/css/style.css
```

O projeto começou como MVC com Thymeleaf e foi refatorado para uma **API REST**. Hoje o Thymeleaf só entrega o `index.html`; todas as interações vão por `fetch` do JavaScript para `/api/*`.

Fluxo de uma requisição: estado escolhido → leitura dos CSVs da pasta da UF → `Comparator` dinâmico → cada algoritmo recebe uma **cópia** do mesmo vetor → tempo e contadores são coletados → previsão é calculada → resposta JSON.

### Métricas coletadas

- **Comparações:** cada chamada ao `Comparator` dentro do laço principal de cada algoritmo.
- **Escritas:** cada atribuição de elemento no vetor (inclui cópias para o vetor auxiliar do Merge Sort).
- **Trocas:** permutas explícitas (Selection Sort e particionamento do Quick Sort).

---

## 🌐 API REST

Os parâmetros são enviados como **form data / query string** (não JSON).

| Método | Rota | Parâmetros | Descrição |
|---|---|---|---|
| `GET` | `/api/estados` | – | Lista as siglas de UF disponíveis. |
| `POST` | `/api/ordenar` | `estado`, `filtros` (um ou mais) | Benchmark dos 4 algoritmos para um estado, amostra ordenada e previsão. |
| `POST` | `/api/comparar-estados` | `estado1`, `estado2`, `filtros` | Benchmark e previsão para dois estados. |

Critérios aceitos em `filtros`: `municipio`, `bioma` e `data`/`data_pas` (sem diferenciar maiúsculas/minúsculas, desde que a validação do `ApiController` esteja normalizando o valor). Qualquer outro valor retorna `400 Bad Request` com a mensagem `O filtro '<valor>' não é válido`.

Respostas de erro (`400 Bad Request`, corpo `{"erro": "..."}`):

- `filtros` ausente ou vazio;
- nenhum registro encontrado para o estado;
- `estado1` igual a `estado2` (comparação);
- dados insuficientes em um dos estados (comparação).

Exemplo:

```bash
curl -X POST http://localhost:8080/api/ordenar \
  -d "estado=AC" -d "filtros=Municipio" -d "filtros=Data"
```

Resposta (resumida):

```json
{
  "estadoSelecionado": "AC",
  "filtrosSelecionados": ["Municipio", "Data"],
  "resultados": {
    "QuickSort":     { "algoritmoNome": "Quick Sort", "tempoMs": 0.0, "estatisticas": { "comparacoes": 0, "escritas": 0, "trocas": 0 } },
    "MergeSort":     { "...": "..." },
    "InsertionSort": { "...": "..." },
    "SelectionSort": { "...": "..." }
  },
  "registrosExemplo": [ ],
  "previsao": { "historicoAgrupado": { "2023-01": 0 }, "interceptoA": 0.0, "inclinacaoB": 0.0,
                "proximoPeriodo": "2025-01", "previsaoProximoPeriodo": 0.0, "tendencia": "Crescente" }
}
```

Se houver menos de 2 meses de dados, a resposta traz `avisoPrevisao` no lugar de `previsao`. Os valores `0` acima são ilustrativos.

---

## 📊 Algoritmos

| Algoritmo | Melhor | Médio | Pior | Espaço aux. | Estável |
|---|---|---|---|---|---|
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Sim |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | Não |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Sim |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) | Não |

### Resultados observados (execução única, Ryzen 5 5600X, 16 GB, Windows 11)

| Registros | Selection | Insertion | Merge | Quick |
|---:|---:|---:|---:|---:|
| 4.143 | 135,2 ms | 64,81 ms | 1,5 ms | 3,7 ms |
| 21.077 | 3.464,7 ms | 2.037,7 ms | 7,6 ms | 78,4 ms |
| 43.992 | 7.073,3 ms | 6.300,5 ms | 35,7 ms | 227,8 ms |
| 97.785 | 42.141,2 ms | 27.489,08 ms | 87,3 ms | 2.059,09 ms |

Cada combinação foi medida **uma única vez**, portanto os tempos não são médias estatísticas. Os resultados completos e as análises estão na documentação do trabalho.

---

## ⚠️ Limitações conhecidas

- Os "filtros" da interface definem **critérios de ordenação**; não há filtro de linhas por município, intervalo de datas ou bioma. O único recorte é o estado.
- Os quatro algoritmos rodam em toda requisição, e os registros exibidos vêm do resultado do Quick Sort.
- O Quick Sort usa particionamento de Lomuto com `<=`, e a rotina de "mediana de três" deixa na prática o **maior** dos três elementos como pivô. Com muitas chaves repetidas (ex.: ordenar por bioma em uma UF de bioma único), as partições ficam desbalanceadas e o tempo se aproxima de O(n²).
- Os CSVs são lidos do disco a cada requisição (e a previsão lê novamente), sem cache.
- A leitura divide as linhas por vírgula (`split(",")`), então campos com vírgula entre aspas não são tratados.
- Os testes automatizados se limitam ao `contextLoads` do Spring; não há testes unitários dos algoritmos.

---

## 📎 Links

- Código: https://github.com/J040Fr0n3/APS---Algoritmos-de-ordenacao---UNIP-2-2026
- Vídeo: _(adicionar link do YouTube)_
- Dados: https://terrabrasilis.dpi.inpe.br/queimadas/bdqueimadas/

## 📚 Referências

- KNUTH, D. E. *The Art of Computer Programming, Vol. 3: Sorting and Searching*. 2. ed. Addison-Wesley, 1998.
- CORMEN, T. H. et al. *Algoritmos: Teoria e Prática*. 3. ed. Elsevier, 2012.
- HOARE, C. A. R. Quicksort. *The Computer Journal*, v. 5, n. 1, 1962.
- INPE. Programa Queimadas – BDQueimadas.
- IBGE. Biomas.