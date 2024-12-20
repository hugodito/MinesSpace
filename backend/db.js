const sqlite3 = require('sqlite3').verbose();
const path = require('path');

const db = new sqlite3.Database(path.join(__dirname, 'sensors.db'), (err) => {
    if (err) {
        console.error('Erreur lors de la connexion à la base de données :', err.message);
    } else {
        console.log('Connecté à la base de données SQLite.');
    }
});

module.exports = db;
db.serialize(() => {
    db.run(`
        CREATE TABLE IF NOT EXISTS sensor_data (
                                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                   temperature REAL,
                                                   pression REAL,
                                                   acceleration REAL,
                                                   vitesse REAL,
                                                   altitude REAL,
                                                   timestamp INTEGER,
                                                   launch_id INTEGER  -- Ajout du champ pour identifier le lancer
        )
    `);
});
