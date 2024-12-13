// Toggle menu
function toggleMenu() {
    const menu = document.querySelector('.menu-items');
    if (menu) {
        menu.classList.toggle('visible');
    }
}

// Gestion du bouton Live
const live = false; // Changez ici pour TRUE ou FALSE
const liveButton = document.getElementById('live-button');
if (live) {
    liveButton.style.color = 'white';
    liveButton.style.backgroundColor = '#47453f';
} else {
    liveButton.style.color = '#f20a29';
    liveButton.style.backgroundColor = '#d4abb1';
}
