// Fonction pour afficher/masquer le menu déroulant
function toggleMenu() {
    const menu = document.querySelector('.menu-items');
    if (menu) {
        menu.classList.toggle('visible'); // Ajouter ou enlever la classe "visible"
    }
}

// Ajout de l'événement au bouton du menu
const menuButton = document.getElementById('menu-button');
if (menuButton) {
    menuButton.addEventListener('click', toggleMenu); // Lien entre le bouton et la fonction toggleMenu
}

// Gestion du bouton Live
const live = false; // Changez ici pour TRUE ou FALSE
const liveButton = document.getElementById('live-button');
if (liveButton) {
    if (live) {
        liveButton.style.color = 'white';
        liveButton.style.backgroundColor = '#47453f';
    } else {
        liveButton.style.color = '#f20a29';
        liveButton.style.backgroundColor = '#d4abb1';
    }
    
    // Rediriger vers live.html lors du clic
    liveButton.addEventListener('click', function() {
        window.location.href = 'live.html';  // Redirection vers la page live.html
    });
}