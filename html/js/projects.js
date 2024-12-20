// Fonction pour charger les données des capteurs par lancer
async function loadSensorDataByLaunch(launchId) {
    const tableBody = document.querySelector(`#sensor-table-${launchId} tbody`);

    try {
        const response = await fetch(`/api/data/${launchId}`);
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

// Fonction pour afficher plusieurs tableaux pour différents lancers
async function loadAllLaunchData() {
    // Pour chaque lancer, crée un tableau et charge ses données
    const launchIds = [1, 2, 3];  // Exemple de liste d'IDs de lancers (tu devrais la récupérer dynamiquement)
    launchIds.forEach((launchId) => {
        const tableContainer = document.createElement('div');
        tableContainer.classList.add('launch-table-container');

        const tableTitle = document.createElement('h3');
        tableTitle.textContent = `Lancer ${launchId}`;

        const table = document.createElement('table');
        table.id = `sensor-table-${launchId}`;
        table.innerHTML = `
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Température</th>
                    <th>Pression</th>
                    <th>Accélération</th>
                    <th>Vitesse</th>
                    <th>Altitude</th>
                    <th>Temps</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        `;

        tableContainer.appendChild(tableTitle);
        tableContainer.appendChild(table);
        document.querySelector('#launch-data-container').appendChild(tableContainer);

        // Charge les données pour ce lancer
        loadSensorDataByLaunch(launchId);
    });
}

// Charger les données pour tous les lancers au démarrage
document.addEventListener('DOMContentLoaded', loadAllLaunchData);
