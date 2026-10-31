function mudarAba(aba) {
    document.getElementById('aba-ordenador').classList.add('hidden');
    document.getElementById('aba-comparador').classList.add('hidden');
    document.getElementById('aba-sobre').classList.add('hidden');

    document.getElementById('btn-ordenador').className = "px-4 py-2 rounded-lg font-medium transition bg-slate-800 text-slate-300 hover:bg-slate-700";
    document.getElementById('btn-comparador').className = "px-4 py-2 rounded-lg font-medium transition bg-slate-800 text-slate-300 hover:bg-slate-700";
    document.getElementById('btn-sobre').className = "px-4 py-2 rounded-lg font-medium transition bg-slate-800 text-slate-300 hover:bg-slate-700";

    if (aba === 'ordenador') {
        document.getElementById('aba-ordenador').classList.remove('hidden');
        document.getElementById('btn-ordenador').className = "px-4 py-2 rounded-lg font-medium transition bg-blue-600 text-white";
    } else if (aba === 'comparador') {
        document.getElementById('aba-comparador').classList.remove('hidden');
        document.getElementById('btn-comparador').className = "px-4 py-2 rounded-lg font-medium transition bg-pink-600 text-white";
    } else if (aba === 'sobre') {
        document.getElementById('aba-sobre').classList.remove('hidden');
        document.getElementById('btn-sobre').className = "px-4 py-2 rounded-lg font-medium transition bg-blue-600 text-white";
    }
}

// Seleção no Mapa do Ordenador                
document.getElementById('svg-mapa-ordenador').addEventListener('click', function(e) {
    const path = e.target.closest('path.estado-mapa');
    if (!path) return;
    
    const uf = path.id; 
    const titleEl = path.querySelector('title');
    const nomeEstado = titleEl ? titleEl.textContent.trim() : uf; 
    
    document.getElementById('input-estado').value = uf;
    document.getElementById('label-estado-selecionado').querySelector('strong').innerText = nomeEstado;
    
    document.querySelectorAll('#svg-mapa-ordenador .estado-mapa').forEach(el => el.classList.remove('selecionado'));
    path.classList.add('selecionado');
});

// Seleção no Mapa do Comparador
let estadosComparacao = []; 
document.getElementById('svg-mapa-comparador').addEventListener('click', function(e) {
    const path = e.target.closest('path.estado-mapa');
    if (!path) return;
    
    const uf = path.id;
    
    if (estadosComparacao.includes(uf)) {
        estadosComparacao = estadosComparacao.filter(item => item !== uf);
    } else {
        if (estadosComparacao.length >= 2) {
            estadosComparacao.shift();
        }
        estadosComparacao.push(uf);
    }

    document.getElementById('input-estado-1').value = estadosComparacao[0] || '';
    document.getElementById('input-estado-2').value = estadosComparacao[1] || '';

    const getNome = (sigla) => {
        if (!sigla) return 'Não selecionado';
        const svgComp = document.getElementById('svg-mapa-comparador');
        const p = svgComp.querySelector(`path#${sigla}`);
        const t = p ? p.querySelector('title') : null;
        return t ? t.textContent.trim() : sigla;
    };

    document.getElementById('lbl-estado-1').innerText = getNome(estadosComparacao[0]);
    document.getElementById('lbl-estado-2').innerText = getNome(estadosComparacao[1]);

    document.querySelectorAll('#svg-mapa-comparador .estado-mapa').forEach(el => {
        el.classList.remove('selecionado');
        el.classList.remove('selecionado-2');
        if (el.id === estadosComparacao[0]) el.classList.add('selecionado');
        if (el.id === estadosComparacao[1]) el.classList.add('selecionado-2');
    });
});

let graficoPrevisaoInstancia = null;
let graficoComparativoInstancia = null;

// Variáveis globais para controle de paginação das tabelas
let registrosGlobaisOrdenador = [];
let paginaAtualOrdenador = 1;
const itensPorPagina = 50;

let registrosGlobaisComp1 = [];
let paginaAtualComp1 = 1;
let registrosGlobaisComp2 = [];
let paginaAtualComp2 = 1;

document.addEventListener("DOMContentLoaded", function() {
    const formOrdenador = document.querySelector("#aba-ordenador form");
    if (formOrdenador) {
        formOrdenador.addEventListener("submit", async function(e) {
            e.preventDefault();
            const formData = new URLSearchParams();
            formData.append("estado", document.getElementById('input-estado').value);
            formOrdenador.querySelectorAll("input[name='filtros']:checked").forEach(cb => formData.append("filtros", cb.value));

            document.body.style.cursor = 'wait';
            try {
                const response = await fetch('/api/ordenar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData.toString()
                });
                const data = await response.json();
                if (!response.ok) { alert(data.erro || "Erro ao processar."); return; }

                renderizarResultadosOrdenador(data);
            } catch (error) {
                console.error("Erro:", error);
            } finally {
                document.body.style.cursor = 'default';
            }
        });
    }

    const formComparador = document.querySelector("#aba-comparador form");
    if (formComparador) {
        formComparador.addEventListener("submit", async function(e) {
            e.preventDefault();
            const formData = new URLSearchParams();
            formData.append("estado1", document.getElementById('input-estado-1').value);
            formData.append("estado2", document.getElementById('input-estado-2').value);
            formComparador.querySelectorAll("input[name='filtros']:checked").forEach(cb => formData.append("filtros", cb.value));

            document.body.style.cursor = 'wait';
            try {
                const response = await fetch('/api/comparar-estados', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData.toString()
                });
                const data = await response.json();
                if (!response.ok) { alert(data.erro || "Erro ao processar."); return; }

                renderizarResultadosComparador(data);
            } catch (error) {
                console.error("Erro:", error);
            } finally {
                document.body.style.cursor = 'default';
            }
        });
    }
});

// Renderização Aba Ordenador
function renderizarResultadosOrdenador(data) {
    let containerBenchmark = document.getElementById("container-benchmark-ordenador");
    if (!containerBenchmark) {
        containerBenchmark = document.createElement("div");
        containerBenchmark.id = "container-benchmark-ordenador";
        containerBenchmark.className = "space-y-4 mt-6";
        document.getElementById("aba-ordenador").appendChild(containerBenchmark);
    }

    let htmlBenchmark = `<h3 class="text-xl font-bold text-slate-800">Benchmark dos Algoritmos de Ordenação</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">`;

    for (const [algoritmo, res] of Object.entries(data.resultados)) {
        htmlBenchmark += `
            <div class="bg-white p-5 rounded-xl shadow-sm border border-slate-200 flex flex-col justify-between">
                <div>
                    <span class="text-xs font-semibold uppercase tracking-wider text-blue-600 bg-blue-50 px-2 py-1 rounded">${algoritmo}</span>
                    <h4 class="text-2xl font-black text-slate-900 mt-3">${res.tempoMs} ms</h4>
                </div>
                <div class="mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-1">
                    <p><strong>Comparações:</strong> ${res.estatisticas.comparacoes}</p>
                    <p><strong>Escritas:</strong> ${res.estatisticas.escritas}</p>
                    <p><strong>Trocas:</strong> ${res.estatisticas.trocas}</p>
                    <p><strong>Total Ops:</strong> ${res.estatisticas.totalOperacoes || 0}</p>
                    <p class="pt-1 border-t"><strong>Registros:</strong> ${res.registrosOrdenados ? res.registrosOrdenados.length : 0}</p>
                </div>
            </div>`;
    }
    htmlBenchmark += `</div>`;
    containerBenchmark.innerHTML = htmlBenchmark;

    let containerPrevisao = document.getElementById("container-previsao-ordenador");
    if (!containerPrevisao) {
        containerPrevisao = document.createElement("div");
        containerPrevisao.id = "container-previsao-ordenador";
        containerPrevisao.className = "space-y-4 mt-6";
        document.getElementById("aba-ordenador").appendChild(containerPrevisao);
    }

    if (data.previsao) {
        containerPrevisao.innerHTML = `
            <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-xl font-bold text-slate-800">Gráfico de Previsão e Tendência</h3>
                    <span class="text-sm font-semibold px-3 py-1 rounded bg-blue-50 text-blue-700">Tendência: ${data.previsao.tendencia}</span>
                </div>
                <div class="relative h-80 w-full"><canvas id="graficoPrevisao"></canvas></div>
            </div>`;

        const ctx1 = document.getElementById('graficoPrevisao').getContext('2d');
        const historico = data.previsao.historicoAgrupado || {};
        let labels = Object.keys(historico);
        let dataValues = Object.values(historico);

        if (data.previsao.proximoPeriodo) {
            labels.push(data.previsao.proximoPeriodo + " (Prev.)");
            dataValues.push(data.previsao.previsaoProximoPeriodo);
        }

        if (graficoPrevisaoInstancia) graficoPrevisaoInstancia.destroy();
        graficoPrevisaoInstancia = new Chart(ctx1, {
            type: 'line',
            data: { labels: labels, datasets: [{ label: 'Histórico & Previsão', data: dataValues, borderColor: '#2563eb', backgroundColor: 'rgba(37, 99, 235, 0.1)', fill: true, tension: 0.3 }] },
            options: { responsive: true, maintainAspectRatio: false }
        });
    } else if (data.avisoPrevisao) {
        containerPrevisao.innerHTML = `<div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200"><div class="text-amber-600 bg-amber-50 p-3 rounded text-sm">${data.avisoPrevisao}</div></div>`;
    }

    // Configura dados para paginação do ordenador
    if (data.registrosExemplo) {
        registrosGlobaisOrdenador = data.registrosExemplo;
        paginaAtualOrdenador = 1;
        criarContainerTabelaOrdenador();
        atualizarTabelaOrdenador();
    }
}

function criarContainerTabelaOrdenador() {
    let containerTabela = document.getElementById("container-tabela-ordenador");
    if (!containerTabela) {
        containerTabela = document.createElement("div");
        containerTabela.id = "container-tabela-ordenador";
        containerTabela.className = "space-y-4 mt-6";
        document.getElementById("aba-ordenador").appendChild(containerTabela);
    }
}

function atualizarTabelaOrdenador() {
    let containerTabela = document.getElementById("container-tabela-ordenador");
    if (!containerTabela) return;

    const inicio = (paginaAtualOrdenador - 1) * itensPorPagina;
    const fim = inicio + itensPorPagina;
    const paginaDados = registrosGlobaisOrdenador.slice(inicio, fim);
    const totalPaginas = Math.ceil(registrosGlobaisOrdenador.length / itensPorPagina) || 1;

    let rowsHtml = '';
    paginaDados.forEach(reg => {
        rowsHtml += `
            <tr class="hover:bg-slate-50">
                <td class="p-3">${reg.pais || 'Brasil'}</td>
                <td class="p-3">${reg.estado || ''}</td>
                <td class="p-3">${reg.municipio || ''}</td>
                <td class="p-3">${reg.bioma || ''}</td>
                <td class="p-3">${reg.dataPas || ''}</td>
            </tr>`;
    });

    containerTabela.innerHTML = `
        <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
            <h3 class="text-xl font-bold text-slate-800">Registros Ordenados (${registrosGlobaisOrdenador.length} total)</h3>
            <div class="overflow-x-auto max-h-96">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr class="bg-slate-100 text-slate-600 text-sm uppercase">
                            <th class="p-3 border-b">País</th>
                            <th class="p-3 border-b">Estado</th>
                            <th class="p-3 border-b">Município</th>
                            <th class="p-3 border-b">Bioma</th>
                            <th class="p-3 border-b">Data</th>
                        </tr>
                    </thead>
                    <tbody class="text-sm divide-y divide-slate-100">${rowsHtml}</tbody>
                </table>
            </div>
            <div class="flex justify-between items-center pt-2">
                <button onclick="mudarPaginaOrdenador(-1)" class="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 rounded font-medium text-sm transition" ${paginaAtualOrdenador === 1 ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>Anterior</button>
                <span class="text-sm text-slate-600">Página <strong>${paginaAtualOrdenador}</strong> de <strong>${totalPaginas}</strong></span>
                <button onclick="mudarPaginaOrdenador(1)" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded font-medium text-sm transition" ${paginaAtualOrdenador >= totalPaginas ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>Próxima</button>
            </div>
        </div>`;
}

function mudarPaginaOrdenador(direcao) {
    const totalPaginas = Math.ceil(registrosGlobaisOrdenador.length / itensPorPagina);
    paginaAtualOrdenador += direcao;
    if (paginaAtualOrdenador < 1) paginaAtualOrdenador = 1;
    if (paginaAtualOrdenador > totalPaginas) paginaAtualOrdenador = totalPaginas;
    atualizarTabelaOrdenador();
}

// Renderização Aba Comparador
function renderizarResultadosComparador(data) {
    let containerComp = document.getElementById("container-resultados-comparador");
    if (!containerComp) {
        containerComp = document.createElement("div");
        containerComp.id = "container-resultados-comparador";
        containerComp.className = "space-y-6 mt-6";
        document.getElementById("aba-comparador").appendChild(containerComp);
    }

    let html = `
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
                <h3 class="text-lg font-bold text-blue-600 border-b pb-2">Benchmark: ${data.estado1Selecionado}</h3>
                <div class="grid grid-cols-2 gap-3">`;
    for (const [alg, res] of Object.entries(data.resultadosEstado1)) {
        html += `
            <div class="bg-slate-50 p-4 rounded-lg border border-slate-100">
                <span class="text-xs font-semibold uppercase text-slate-500">${alg}</span>
                <p class="text-xl font-bold text-slate-900 mt-1">${res.tempoMs} ms</p>
                <div class="mt-2 text-xs text-slate-600 space-y-0.5">
                    <p>Comp: ${res.estatisticas.comparacoes}</p>
                    <p>Escr: ${res.estatisticas.escritas}</p>
                    <p>Trocas: ${res.estatisticas.trocas}</p>
                    <p>Regs: ${res.registrosOrdenados ? res.registrosOrdenados.length : 0}</p>
                </div>
            </div>`;
    }
    html += `</div></div>`;

    html += `
            <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
                <h3 class="text-lg font-bold text-pink-600 border-b pb-2">Benchmark: ${data.estado2Selecionado}</h3>
                <div class="grid grid-cols-2 gap-3">`;
    for (const [alg, res] of Object.entries(data.resultadosEstado2)) {
        html += `
            <div class="bg-slate-50 p-4 rounded-lg border border-slate-100">
                <span class="text-xs font-semibold uppercase text-slate-500">${alg}</span>
                <p class="text-xl font-bold text-slate-900 mt-1">${res.tempoMs} ms</p>
                <div class="mt-2 text-xs text-slate-600 space-y-0.5">
                    <p>Comp: ${res.estatisticas.comparacoes}</p>
                    <p>Escr: ${res.estatisticas.escritas}</p>
                    <p>Trocas: ${res.estatisticas.trocas}</p>
                    <p>Regs: ${res.registrosOrdenados ? res.registrosOrdenados.length : 0}</p>
                </div>
            </div>`;
    }
    html += `</div></div></div>`;

    html += `
        <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
            <h3 class="text-xl font-bold text-slate-800 mb-4">Gráfico Comparativo de Previsão (Tendências)</h3>
            <div class="bg-indigo-50 p-4 rounded-xl border border-indigo-200 text-center text-sm font-semibold text-indigo-900 shadow-sm mb-4">
                Tendência ${data.estado1Selecionado}: <strong class="text-blue-600">${data.previsaoEstado1 ? data.previsaoEstado1.tendencia : 'N/A'}</strong> | 
                Tendência ${data.estado2Selecionado}: <strong class="text-pink-600">${data.previsaoEstado2 ? data.previsaoEstado2.tendencia : 'N/A'}</strong>
            </div>
            <div class="relative h-80 w-full"><canvas id="graficoComparativoPrevisao"></canvas></div>
        </div>`;

    // Container para as tabelas lado a lado paginadas
    html += `
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div id="container-tabela-comp1"></div>
            <div id="container-tabela-comp2"></div>
        </div>`;

    containerComp.innerHTML = html;

    // Guarda registros globais para paginação do comparador
    registrosGlobaisComp1 = data.registrosEstado1 || [];
    paginaAtualComp1 = 1;
    atualizarTabelaComp1(data.estado1Selecionado);

    registrosGlobaisComp2 = data.registrosEstado2 || [];
    paginaAtualComp2 = 1;
    atualizarTabelaComp2(data.estado2Selecionado);

    // Gráfico Comparativo Chart.js
    const ctx2 = document.getElementById('graficoComparativoPrevisao').getContext('2d');
    let labelsSet = new Set();
    if (data.previsaoEstado1?.historicoAgrupado) Object.keys(data.previsaoEstado1.historicoAgrupado).forEach(k => labelsSet.add(k));
    if (data.previsaoEstado2?.historicoAgrupado) Object.keys(data.previsaoEstado2.historicoAgrupado).forEach(k => labelsSet.add(k));
    let labels = Array.from(labelsSet).sort();

    let data1 = labels.map(label => data.previsaoEstado1?.historicoAgrupado?.[label.replace(" (Prev.)", "")] ?? null);
    let data2 = labels.map(label => data.previsaoEstado2?.historicoAgrupado?.[label.replace(" (Prev.)", "")] ?? null);

    if (graficoComparativoInstancia) graficoComparativoInstancia.destroy();
    graficoComparativoInstancia = new Chart(ctx2, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                { label: data.estado1Selecionado, data: data1, borderColor: '#3b82f6', backgroundColor: 'rgba(59, 130, 246, 0.05)', tension: 0.3, spanGaps: true },
                { label: data.estado2Selecionado, data: data2, borderColor: '#f43f5e', backgroundColor: 'rgba(244, 63, 94, 0.05)', tension: 0.3, spanGaps: true }
            ]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
}

function atualizarTabelaComp1(nomeEstado) {
    const el = document.getElementById("container-tabela-comp1");
    if (!el) return;
    const inicio = (paginaAtualComp1 - 1) * itensPorPagina;
    const paginaDados = registrosGlobaisComp1.slice(inicio, inicio + itensPorPagina);
    const totalPaginas = Math.ceil(registrosGlobaisComp1.length / itensPorPagina) || 1;

    let rows = '';
    paginaDados.forEach(r => rows += `<tr class="hover:bg-slate-50"><td class="p-2">${r.municipio || ''}</td><td class="p-2">${r.bioma || ''}</td><td class="p-2">${r.dataPas || ''}</td></tr>`);

    el.innerHTML = `
        <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
            <h3 class="font-bold text-slate-800">Registros - ${nomeEstado}</h3>
            <div class="overflow-x-auto max-h-80"><table class="w-full text-left text-sm border-collapse"><thead><tr class="bg-slate-100 text-slate-600 uppercase text-xs"><th class="p-2">Município</th><th class="p-2">Bioma</th><th class="p-2">Data</th></tr></thead><tbody class="divide-y divide-slate-100">${rows}</tbody></table></div>
            <div class="flex justify-between items-center pt-2">
                <button onclick="mudarPaginaComp(1, -1)" class="px-3 py-1 bg-slate-200 hover:bg-slate-300 text-slate-700 rounded text-xs font-medium" ${paginaAtualComp1 === 1 ? 'disabled style="opacity: 0.5;"' : ''}>Anterior</button>
                <span class="text-xs text-slate-600">Pág ${paginaAtualComp1}/${totalPaginas}</span>
                <button onclick="mudarPaginaComp(1, 1)" class="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white rounded text-xs font-medium" ${paginaAtualComp1 >= totalPaginas ? 'disabled style="opacity: 0.5;"' : ''}>Próxima</button>
            </div>
        </div>`;
}

function atualizarTabelaComp2(nomeEstado) {
    const el = document.getElementById("container-tabela-comp2");
    if (!el) return;
    const inicio = (paginaAtualComp2 - 1) * itensPorPagina;
    const paginaDados = registrosGlobaisComp2.slice(inicio, inicio + itensPorPagina);
    const totalPaginas = Math.ceil(registrosGlobaisComp2.length / itensPorPagina) || 1;

    let rows = '';
    paginaDados.forEach(r => rows += `<tr class="hover:bg-slate-50"><td class="p-2">${r.municipio || ''}</td><td class="p-2">${r.bioma || ''}</td><td class="p-2">${r.dataPas || ''}</td></tr>`);

    el.innerHTML = `
        <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
            <h3 class="font-bold text-slate-800">Registros - ${nomeEstado}</h3>
            <div class="overflow-x-auto max-h-80"><table class="w-full text-left text-sm border-collapse"><thead><tr class="bg-slate-100 text-slate-600 uppercase text-xs"><th class="p-2">Município</th><th class="p-2">Bioma</th><th class="p-2">Data</th></tr></thead><tbody class="divide-y divide-slate-100">${rows}</tbody></table></div>
            <div class="flex justify-between items-center pt-2">
                <button onclick="mudarPaginaComp(2, -1)" class="px-3 py-1 bg-slate-200 hover:bg-slate-300 text-slate-700 rounded text-xs font-medium" ${paginaAtualComp2 === 1 ? 'disabled style="opacity: 0.5;"' : ''}>Anterior</button>
                <span class="text-xs text-slate-600">Pág ${paginaAtualComp2}/${totalPaginas}</span>
                <button onclick="mudarPaginaComp(2, 1)" class="px-3 py-1 bg-pink-600 hover:bg-pink-700 text-white rounded text-xs font-medium" ${paginaAtualComp2 >= totalPaginas ? 'disabled style="opacity: 0.5;"' : ''}>Próxima</button>
            </div>
        </div>`;
}

function mudarPaginaComp(tabelaId, direcao) {
    if (tabelaId === 1) {
        const total = Math.ceil(registrosGlobaisComp1.length / itensPorPagina);
        paginaAtualComp1 += direcao;
        if (paginaAtualComp1 < 1) paginaAtualComp1 = 1;
        if (paginaAtualComp1 > total) paginaAtualComp1 = total;
        atualizarTabelaComp1(document.getElementById('lbl-estado-1').innerText);
    } else {
        const total = Math.ceil(registrosGlobaisComp2.length / itensPorPagina);
        paginaAtualComp2 += direcao;
        if (paginaAtualComp2 < 1) paginaAtualComp2 = 1;
        if (paginaAtualComp2 > total) paginaAtualComp2 = total;
        atualizarTabelaComp2(document.getElementById('lbl-estado-2').innerText);
    }
}