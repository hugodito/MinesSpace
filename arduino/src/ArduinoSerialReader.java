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
        SerialPort comPort = SerialPort.getCommPort("COM8");
        comPort.setBaudRate(9600);

        if (comPort.openPort()) {
            System.out.println("Port série ouvert avec succès.");
        } else {
            System.out.println("Échec de l'ouverture du port série.");
            return;
        }

        StringBuilder dataBuffer = new StringBuilder();

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

                byte[] newData = new byte[comPort.bytesAvailable()];
                comPort.readBytes(newData, newData.length);
                dataBuffer.append(new String(newData));

                // Vérifier si le buffer contient un JSON complet
                String rawData = dataBuffer.toString();
                int startIndex = rawData.indexOf('{'); // Trouver le début du JSON
                int endIndex = rawData.lastIndexOf('}'); // Trouver la fin du JSON

                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    String jsonString = rawData.substring(startIndex, endIndex + 1); // Extraire le JSON
                    dataBuffer.setLength(0); // Vider le buffer après extraction

                    System.out.println("Données complètes reçues : " + jsonString);

                    // Traiter le JSON extrait
                    try {
                        JSONObject jsonReceived = new JSONObject(jsonString);

                        int temperature = jsonReceived.getInt("temperature");
                        double pression = jsonReceived.getDouble("pression");
                        double acceleration = jsonReceived.getDouble("acceleration");
                        int vitesse = jsonReceived.getInt("vitesse");
                        int altitude = jsonReceived.getInt("altitude");

                        // Construire un JSON pour l'envoi
                        String jsonData = new JSONObject()
                                .put("temperature", temperature)
                                .put("pression", pression)
                                .put("acceleration", acceleration)
                                .put("vitesse", vitesse)
                                .put("altitude", altitude)
                                .put("launch_id", 4) // Ajoutez un launch_id par défaut ou configurable
                                .toString();

                        // Envoyer au serveur
                        sendToServer(jsonData);

                    } catch (Exception e) {
                        System.err.println("Erreur lors du traitement des données JSON : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            comPort.closePort();
            System.out.println("Port série fermé proprement.");
        }));

        System.out.println("Le programme fonctionne indéfiniment. Appuyez sur Ctrl+C pour arrêter.");

        // ✅ Ajouter une boucle infinie pour empêcher l'arrêt du programme
        while (true) {
            try {
                Thread.sleep(1000); // Pause pour réduire l'utilisation du CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendToServer(String jsonData) {
        try {
            URL statusUrl = new URL("http://localhost:3000/api/recording/status");
            HttpURLConnection statusConn = (HttpURLConnection) statusUrl.openConnection();
            statusConn.setRequestMethod("GET");
            int statusResponseCode = statusConn.getResponseCode();

            if (statusResponseCode == 200) {
                // Lire la réponse pour vérifier l'état d'enregistrement
                Scanner scanner = new Scanner(statusConn.getInputStream());
                String jsonResponse = scanner.nextLine();
                scanner.close();

                JSONObject statusJson = new JSONObject(jsonResponse);
                boolean isRecording = statusJson.getBoolean("isRecording");

                if (!isRecording) {
                    System.out.println("Enregistrement désactivé. Données ignorées.");
                    return; // Arrêter ici si l'enregistrement est désactivé
                }
            }

            URL url = new URL("http://localhost:3000/api/data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Réponse du serveur : " + responseCode);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi des données au serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
