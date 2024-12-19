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


// Route pour servir index.html
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, '../html/index.html'));
});

// Démarrage du serveur
app.listen(PORT, () => {
    console.log(`Serveur démarré sur http://localhost:${PORT}`);
});

sensorDataModel.insertData(22.5, 1013, 0.98, 15, 150).then((id) => {
    console.log(`Données insérées avec l'ID ${id}`);
}).catch((err) => {
    console.error('Erreur lors de l’insertion des données factices :', err);
});
