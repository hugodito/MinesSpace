// Importation des bibliothèques nécessaires
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.Scanner;

public class ArduinoSerialReader {

    public static void main(String[] args) {
        // Initialisation du port série COM8 avec un taux de transmission de 9600 bauds
        SerialPort comPort = SerialPort.getCommPort("COM8");
        comPort.setBaudRate(9600);

        // Ouverture du port série
        if (comPort.openPort()) {
            System.out.println("Port série ouvert avec succès.");
        } else {
            System.out.println("Échec de l'ouverture du port série.");
            return;
        }

        StringBuilder dataBuffer = new StringBuilder();

        // Ajout d'un écouteur d'événements pour détecter les données disponibles
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; // Écoute des données disponibles
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

                // Lecture des données disponibles dans le buffer
                byte[] newData = new byte[comPort.bytesAvailable()];
                comPort.readBytes(newData, newData.length);
                dataBuffer.append(new String(newData));

                // Vérification et extraction du JSON complet
                String rawData = dataBuffer.toString();
                int startIndex = rawData.indexOf('{'); // Début du JSON
                int endIndex = rawData.lastIndexOf('}'); // Fin du JSON

                // Si un JSON complet est trouvé, on le traite
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    String jsonString = rawData.substring(startIndex, endIndex + 1);
                    dataBuffer.setLength(0); // Vidage du buffer

                    System.out.println("Données complètes reçues : " + jsonString);

                    // Traitement du JSON reçu
                    try {
                        JSONObject jsonReceived = new JSONObject(jsonString);

                        // Extraction des données du JSON
                        int temperature = jsonReceived.getInt("temperature");
                        double pression = jsonReceived.getDouble("pression");
                        double acceleration = jsonReceived.getDouble("acceleration");
                        int vitesse = jsonReceived.getInt("vitesse");
                        int altitude = jsonReceived.getInt("altitude");

                        // Construction d'un JSON pour l'envoi
                        String jsonData = new JSONObject()
                                .put("temperature", temperature)
                                .put("pression", pression)
                                .put("acceleration", acceleration)
                                .put("vitesse", vitesse)
                                .put("altitude", altitude)
                                .put("launch_id", 4) // Identifiant de lancement (statique dans ce cas)
                                .toString();

                        // Envoi des données au serveur
                        sendToServer(jsonData);

                    } catch (Exception e) {
                        System.err.println("Erreur lors du traitement des données JSON : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        // Fermeture propre du port lors de l'arrêt du programme
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            comPort.closePort();
            System.out.println("Port série fermé proprement.");
        }));

        System.out.println("Le programme fonctionne indéfiniment. Appuyez sur Ctrl+C pour arrêter.");

        // Boucle infinie pour maintenir le programme actif
        while (true) {
            try {
                Thread.sleep(1000); // Pause pour limiter l'utilisation du CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Fonction d'envoi des données au serveur
    private static void sendToServer(String jsonData) {
        try {
            // Vérification de l'état d'enregistrement via une requête GET
            URL statusUrl = new URL("http://localhost:3000/api/recording/status");
            HttpURLConnection statusConn = (HttpURLConnection) statusUrl.openConnection();
            statusConn.setRequestMethod("GET");
            int statusResponseCode = statusConn.getResponseCode();

            if (statusResponseCode == 200) {
                // Vérification de la réponse pour savoir si l'enregistrement est actif
                Scanner scanner = new Scanner(statusConn.getInputStream());
                String jsonResponse = scanner.nextLine();
                scanner.close();

                JSONObject statusJson = new JSONObject(jsonResponse);
                boolean isRecording = statusJson.getBoolean("isRecording");

                if (!isRecording) {
                    System.out.println("Enregistrement désactivé. Données ignorées.");
                    return; // Si l'enregistrement est désactivé, ne pas envoyer les données
                }
            }

            // Envoi des données au serveur via une requête POST
            URL url = new URL("http://localhost:3000/api/data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Vérification de la réponse du serveur
            int responseCode = conn.getResponseCode();
            System.out.println("Réponse du serveur : " + responseCode);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi des données au serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
