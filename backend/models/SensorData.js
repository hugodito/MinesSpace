const db = require('../db');

// Fonction pour insérer des données
const insertData = (temperature, pression) => {
    return new Promise((resolve, reject) => {
        const query = `INSERT INTO sensor_data (temperature, pression) VALUES (?, ?)`;
        db.run(query, [temperature, pression], function (err) {
            if (err) {
                reject(err);
            } else {
                resolve(this.lastID); // Renvoie l'ID de la ligne insérée
            }
        });
    });
};

// Fonction pour récupérer toutes les données
const getAllData = () => {
    return new Promise((resolve, reject) => {
        const query = `SELECT * FROM sensor_data ORDER BY timestamp DESC`;
        db.all(query, [], (err, rows) => {
            if (err) {
                reject(err);
            } else {
                resolve(rows);
            }
        });
    });
};

module.exports = { insertData, getAllData };
