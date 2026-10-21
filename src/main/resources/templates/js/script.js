function navegarPara(PaginaDesejada) {
    // Pega todas as divs que têm a classe 'pagina' e esconde todas
    const todasPaginas = document.querySelectorAll('.page');
    todasPaginas.forEach(pagina => {
        pagina.classList.remove('active');
    });

    // Pega apenas a página que o usuário quer ver e a ativa (torna visível)
    const paginaAlvo = document.getElementById(PaginaDesejada);
    if (paginaAlvo) {
        paginaAlvo.classList.add('active');
    }

}
