// Função para realçar itens de menu ativos
document.addEventListener('DOMContentLoaded', function() {
    // Obtém o caminho atual da URL
    const currentPath = window.location.pathname;

    // Seleciona todos os links de navegação
    const navLinks = document.querySelectorAll('.mdl-navigation__link');

    // Para cada link, verifica se o href corresponde ao caminho atual
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (currentPath === href || currentPath.startsWith(href) && href !== '/') {
            link.classList.add('is-active');
            link.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
        }
    });
});