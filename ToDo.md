Programmation Embarquée :
- Emetteur :
    - Récupère les données des capteurs, les stocke (en cas de défaillance du direct en LoRa pour pas perdre les données) et les envoie au recepteur
- Recepteur : 
    - Récupère les données de l'émetteur via LoRa et les envoie sur un wifi
    
Programmation Web :
- Backend : Récupère les données du récepteur, les stocke dans un API et les publie sur un site (pas celui de MinesSpace)
- Frontend avec Live, historique, A propos, Actus, Contact
- Courbes de l'accélération, vitesse, altitude, orientation se trace en direct puis stockées en historique

Programmation Mobile :
- Idem que Web mais en Kotlin et présentation différente, adaptée au format mobile

pour lancer le serveur (il faut être dans le dossier MinesSpace) soit : 
#1 :
npm install (si pas déjà fait)
npm start

ou #2 :
node backend/server.js