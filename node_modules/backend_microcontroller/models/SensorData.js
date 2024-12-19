const db = require('../db');

// Fonction pour insérer des données
function insertData(temperature, pression, acceleration, vitesse, altitude) {
    return new Promise((resolve, reject) => {
        const query = `
            INSERT INTO sensor_data (temperature, pression, acceleration, vitesse, altitude, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
        `;
        db.run(query, [temperature, pression, acceleration, vitesse, altitude, Date.now()], function (err) {
            if (err) {
                console.error('Erreur lors de l’insertion des données :', err.message);
                reject(err);
            } else {
                resolve(this.lastID);
            }
        });
    });
}

// Fonction pour récupérer toutes les données
function getAllData() {
    return new Promise((resolve, reject) => {
        db.all('SELECT * FROM sensor_data ORDER BY timestamp DESC', [], (err, rows) => {
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
                resolve(this.changes);  // Retourne le nombre de lignes supprimées
            }
        });
    });
}

module.exports = {
    insertData,
    getAllData,
    deleteData,  // Ajouter la fonction deleteData
};


