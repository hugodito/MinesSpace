///////////////Gestion des outils du HEADER////////////////////////////////////

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


////////////////////////// GESTION DU LOGIN 
// Gestion du formulaire
document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("login-form");

    if (loginForm) { // Vérifie si le formulaire existe avant d'ajouter l'écouteur
        loginForm.addEventListener("submit", async function(event) {
            event.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            try {
                // Chargement des utilisateurs depuis users.json
                const response = await fetch("resources/users.json");
                const users = await response.json();

                // Vérification des identifiants
                const user = users.find(user => user.username === username && user.password === password);

                if (user) {
                    alert("Connexion réussie !");
                    window.location.href = "index.html"; // Redirection après succès
                    localStorage.setItem('user', JSON.stringify(user));  // Stocker l'utilisateur dans localStorage
                } else {
                    alert("Nom d'utilisateur ou mot de passe incorrect !");
                }
            } catch (error) {
                console.error("Erreur lors de la vérification des utilisateurs :", error);
                alert("Impossible de vérifier les identifiants. Veuillez réessayer plus tard.");
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    // Vérifier si l'utilisateur est connecté
    const user = JSON.parse(localStorage.getItem('user'));  // Vérifier si l'utilisateur est stocké dans le localStorage

    // Mettre à jour l'interface en fonction de l'état de la connexion
    if (user) {
        // Si l'utilisateur est connecté, afficher le bouton "Se déconnecter"
        document.getElementById('login-logout-button').textContent = 'Se déconnecter';
        document.getElementById('login-logout-button').onclick = logout; // Définir la fonction logout
    } else {
        // Si l'utilisateur n'est pas connecté, afficher le bouton "Login"
        document.getElementById('login-logout-button').textContent = 'Login';
        document.getElementById('login-logout-button').onclick = login; // Définir la fonction login
    }

    // Gestion de l'affichage des contrôles administrateurs
    const adminControls = document.getElementById('admin-controls');
    if (adminControls) {
        if (user && user.role === 'admin') {
            adminControls.style.display = 'block';
        } else {
            adminControls.style.display = 'none';
        }
    }
});

// Fonction pour gérer la déconnexion
function logout() {
    // Retirer les informations d'utilisateur du localStorage
    localStorage.removeItem('user');

    // Afficher un message de confirmation
    alert('Vous êtes déconnecté');

    // Rediriger vers la page d'accueil
    window.location.href = 'index.html';
}

// Fonction pour gérer la connexion
function login() {
    // Rediriger l'utilisateur vers la page de connexion
    window.location.href = 'login.html';
}
