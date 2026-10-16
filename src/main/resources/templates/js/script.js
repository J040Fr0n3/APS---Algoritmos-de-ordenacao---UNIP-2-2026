function navegarPara(idDaPaginaDesejada) {
    // Pega todas as divs que têm a classe 'pagina' e esconde todas
    const todasAsPaginas = document.querySelectorAll('.pagina');
    todasAsPaginas.forEach(pagina => {
        pagina.classList.remove('ativa');
    });

    // Pega apenas a página que o usuário quer ver e a ativa (torna visível)
    const paginaAlvo = document.getElementById(idDaPaginaDesejada);
    if (paginaAlvo) {
        paginaAlvo.classList.add('ativa');
    }

}
