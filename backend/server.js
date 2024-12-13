const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const sensorDataModel = require('./models/sensorData'); // Import du modèle
const path = require('path');

// Initialisation de l'application
const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Route POST : Enregistrer des données
app.post('/api/data', async (req, res) => {
    const { temperature, pression } = req.body;
    try {
        const result = await sensorDataModel.insertData(temperature, pression);
        res.status(201).json({ message: 'Données enregistrées avec succès.', id: result });
    } catch (error) {
        console.error('Erreur lors de l’insertion des données :', error);
        res.status(500).json({ message: 'Erreur serveur.' });
    }
});

// Route GET : Récupérer des données
app.get('/api/data', async (req, res) => {
    try {
        const data = await sensorDataModel.getAllData();
        res.status(200).json(data);
    } catch (error) {
        console.error('Erreur lors de la récupération des données :', error);
        res.status(500).json({ message: 'Erreur serveur.' });
    }
});

// Route pour servir index.html situé dans MinesSpace
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, '../html/index.html'));
});

// Démarrage du serveur
app.listen(PORT, () => {
    console.log(`Serveur démarré sur http://localhost:${PORT}`);
});
