// Fonction pour charger les données des capteurs
async function loadSensorData() {
    const tableBody = document.querySelector('#sensor-table tbody');

    try {
        const response = await fetch('/api/data'); // Récupère les données depuis l'API du serveur
        const data = await response.json();

        if (Array.isArray(data)) {
            // Si des données sont disponibles, crée une ligne pour chaque entrée
            data.forEach((sensor) => {
                const row = document.createElement('tr');

                row.innerHTML = `
                    <td>${sensor.id}</td>
                    <td>${sensor.temperature.toFixed(2)}</td>
                    <td>${sensor.pression.toFixed(2)}</td>
                    <td>${sensor.acceleration.toFixed(2)}</td>
                    <td>${sensor.vitesse.toFixed(2)}</td>
                    <td>${sensor.altitude.toFixed(2)}</td>
                    <td>${new Date(sensor.timestamp).toLocaleString()}</td>
                    <td>
                        <button class="delete-btn" data-id="${sensor.id}">Supprimer</button>
                    </td>
                `;

                // Ajouter un événement au bouton de suppression
                row.querySelector('.delete-btn').addEventListener('click', async () => {
                    await deleteSensorData(sensor.id);
                    row.remove();  // Retirer la ligne du tableau après suppression
                });

                tableBody.appendChild(row);
            });
        } else {
            // Si aucune donnée n'est disponible
            tableBody.innerHTML = `<tr><td colspan="8">Aucune donnée disponible.</td></tr>`;
        }
    } catch (error) {
        // En cas d'erreur lors de la récupération des données
        console.error('Erreur lors de la récupération des données :', error);
        tableBody.innerHTML = `<tr><td colspan="8">Erreur lors du chargement des données.</td></tr>`;
    }
}

// Fonction pour supprimer des données
async function deleteSensorData(id) {
    try {
        const response = await fetch(`/api/data/${id}`, {
            method: 'DELETE',  // Méthode DELETE pour supprimer la donnée
        });

        if (!response.ok) {
            throw new Error('Erreur lors de la suppression des données');
        }

        const data = await response.json();
        console.log(data.message);  // Afficher le message de succès
    } catch (error) {
        console.error('Erreur lors de la suppression des données :', error);
    }
}

// Charger les données au démarrage
document.addEventListener('DOMContentLoaded', loadSensorData);
