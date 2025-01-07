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

let isRecording = false;

// Route pour basculer l'état d'enregistrement
app.post('/api/recording', (req, res) => {
    isRecording = req.body.isRecording; // Attendez un booléen dans le body
    console.log(`Enregistrement ${isRecording ? 'activé' : 'désactivé'}`);
    res.json({ success: true, isRecording });
});


// Route POST : Enregistrer des données
app.post('/api/data', async (req, res) => {
    if (!isRecording) {
        return res.status(403).json({ error: "L'enregistrement des données est désactivé." });
    }
    try {
        const { temperature, pression, acceleration, vitesse, altitude, launch_id } = req.body;

        // Vérification des données reçues
        if (
            temperature === undefined || pression === undefined ||
            acceleration === undefined || vitesse === undefined ||
            altitude === undefined || launch_id === undefined
        ) {
            console.log("Données reçues incomplètes ou invalides :", req.body);
            return res.status(400).send({ error: "Données incomplètes ou au mauvais format" });
        }

        // Vérification des types de données
        if (
            typeof temperature !== 'number' || typeof pression !== 'number' ||
            typeof acceleration !== 'number' || typeof vitesse !== 'number' ||
            typeof altitude !== 'number' || typeof launch_id !== 'number'
        ) {
            console.log("Données avec des types invalides :", req.body);
            return res.status(400).send({ error: "Types de données incorrects" });
        }

        // Insérer les données dans la base SQLite
        const id = await sensorDataModel.insertData(temperature, pression, acceleration, vitesse, altitude, launch_id);
        console.log("Données insérées avec succès, ID :", id);
        res.status(201).send({ success: true, id });

    } catch (error) {
        console.error('Erreur lors de l’insertion des données :', error);
        res.status(500).send({ error: "Erreur serveur" });
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

// Route pour récupérer les données par launch_id
app.get('/api/data/:launch_id', async (req, res) => {
    const { launch_id } = req.params;  // Récupère l'ID du lancement
    try {
        const data = await sensorDataModel.getDataByLaunchId(launch_id);
        res.status(200).json(data);
    } catch (error) {
        console.error('Erreur lors de la récupération des données :', error);
        res.status(500).json({ message: 'Erreur serveur.' });
    }
});

app.get('/api/recording/status', (req, res) => {
    res.json({ isRecording });
});


// Démarrage du serveur
app.listen(PORT, () => {
    console.log(`Serveur démarré sur http://localhost:${PORT}`);

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
