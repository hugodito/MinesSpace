const db = require('../db');

// Fonction pour insérer des données
function insertData(temperature, pression, acceleration, vitesse, altitude, launch_id) {
    return new Promise((resolve, reject) => {
        // Validation des données
        if (
            typeof temperature !== 'number' || typeof pression !== 'number' ||
            typeof acceleration !== 'number' || typeof vitesse !== 'number' ||
            typeof altitude !== 'number' || typeof launch_id !== 'number'
        ) {
            console.error("Les données fournies sont invalides :", {
                temperature, pression, acceleration, vitesse, altitude, launch_id
            });
            return reject(new Error("Les données fournies sont invalides."));
        }

        // Requête d'insertion
        const query = `
            INSERT INTO sensor_data (temperature, pression, acceleration, vitesse, altitude, timestamp, launch_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        `;

        db.run(query, [temperature, pression, acceleration, vitesse, altitude, Date.now(), launch_id], function (err) {
            if (err) {
                console.error('Erreur lors de l’insertion des données dans la base :', err.message);
                reject(err);
            } else {
                console.log('Données insérées avec succès, ID :', this.lastID);
                resolve(this.lastID);
            }
        });
    });
}

// Fonction pour récupérer toutes les données
function getAllData(limit = 100, offset = 0) {
    return new Promise((resolve, reject) => {
        const query = `
            SELECT * FROM sensor_data 
            ORDER BY timestamp DESC 
            LIMIT ? OFFSET ?
        `;

        db.all(query, [limit, offset], (err, rows) => {
            if (err) {
                console.error('Erreur lors de la récupération des données :', err.message);
                reject(err);
            } else {
                resolve(rows);
            }
        });
    });
}

// Fonction pour supprimer une donnée par ID
function deleteData(id) {
    return new Promise((resolve, reject) => {
        const query = 'DELETE FROM sensor_data WHERE id = ?';
        db.run(query, [id], function (err) {
            if (err) {
                console.error('Erreur lors de la suppression des données :', err.message);
                reject(err);
            } else {
                console.log('Nombre de lignes supprimées :', this.changes);
                resolve(this.changes);
            }
        });
    });
}

// Fonction pour récupérer les données d'un lancer spécifique
function getDataByLaunchId(launch_id) {
    return new Promise((resolve, reject) => {
        const query = `
            SELECT * FROM sensor_data 
            WHERE launch_id = ? 
            ORDER BY timestamp ASC
        `;
        db.all(query, [launch_id], (err, rows) => {
            if (err) {
                console.error('Erreur lors de la récupération des données :', err.message);
                reject(err);
            } else {
                resolve(rows);
            }
        });
    });
}

module.exports = {
    insertData,
    getAllData,
    deleteData,
    getDataByLaunchId,
};
