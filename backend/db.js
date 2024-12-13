const sqlite3 = require('sqlite3').verbose();
const path = require('path');

// Connexion à la base de données SQLite
const dbPath = path.resolve(__dirname, 'data.db');
const db = new sqlite3.Database(dbPath, (err) => {
    if (err) {
        console.error('Erreur lors de la connexion à SQLite :', err);
    } else {
        console.log('Connecté à la base de données SQLite.');
    }
});

// Création de la table si elle n'existe pas
db.serialize(() => {
    db.run(`
        CREATE TABLE IF NOT EXISTS sensor_data (
                                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                   temperature REAL,
                                                   pression REAL,
                                                   timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    `, (err) => {
        if (err) {
            console.error('Erreur lors de la création de la table :', err);
        } else {
            console.log('Table sensor_data prête.');
        }
    });
});

module.exports = db;
