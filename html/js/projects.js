// Fonction pour charger les données des capteurs
async function loadSensorData() {
    const tableBody = document.querySelector('#sensor-table tbody');

    try {
        const response = await fetch('/api/data');
        const data = await response.json();

        if (Array.isArray(data)) {
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
                `;

                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = `<tr><td colspan="7">Aucune donnée disponible.</td></tr>`;
        }
    } catch (error) {
        console.error('Erreur lors de la récupération des données :', error);
        tableBody.innerHTML = `<tr><td colspan="7">Erreur lors du chargement des données.</td></tr>`;
    }
}

// Charger les données au démarrage
document.addEventListener('DOMContentLoaded', loadSensorData);