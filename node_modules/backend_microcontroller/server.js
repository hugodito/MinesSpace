const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path');
const sensorDataModel = require('./models/sensorData');

// Initialisation de l'application
const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Middleware pour servir les fichiers statiques depuis 'html'
app.use(express.static(path.join(__dirname, '../html'))); // Cela permet de servir les fichiers CSS, JS, images, etc.

// Ajouter des données factices si la base est vide
async function addFakeData() {
    const fakeData = [
        { temperature: 22.5, pression: 1013, acceleration: 0.98, vitesse: 15, altitude: 150 },
        { temperature: 23.0, pression: 1012, acceleration: 1.05, vitesse: 16, altitude: 160 },
        { temperature: 21.8, pression: 1010, acceleration: 1.02, vitesse: 14, altitude: 145 },
    ];

    // Insérer des données factices dans la base
    for (const data of fakeData) {
        try {
            await sensorDataModel.insertData(data.temperature, data.pression, data.acceleration, data.vitesse, data.altitude);
            console.log('Données factices insérées');
        } catch (err) {
            console.error('Erreur lors de l\'insertion des données factices :', err);
        }
    }
}

// Route POST : Enregistrer des données
app.post('/api/data', async (req, res) => {
    const { temperature, pression, acceleration, vitesse, altitude } = req.body;
    try {
        const result = await sensorDataModel.insertData(temperature, pression, acceleration, vitesse, altitude);
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

// Route pour servir index.html
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, '../html/index.html'));
});

// Démarrage du serveur
app.listen(PORT, () => {
    console.log(`Serveur démarré sur http://localhost:${PORT}`);

    // Ajouter des données factices lors du démarrage du serveur
    addFakeData();
});

// Route DELETE : Supprimer des données par ID
app.delete('/api/data/:id', async (req, res) => {
    const { id } = req.params;
    try {
        // Utiliser la méthode du modèle pour supprimer une donnée par ID
        await sensorDataModel.deleteData(id);
        res.status(200).json({ message: `Données avec l'ID ${id} supprimées avec succès.` });
    } catch (error) {
        console.error('Erreur lors de la suppression des données :', error);
        res.status(500).json({ message: 'Erreur serveur.' });
    }
});
