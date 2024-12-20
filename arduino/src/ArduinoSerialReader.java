import com.fazecast.jSerialComm.SerialPort;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class ArduinoSerialReader {

    public static void main(String[] args) {
        // Remplace "COM3" par le port série de ton ESP32
        SerialPort comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(115200); // Assure-toi que cela correspond à la configuration de ton ESP32

        if (comPort.openPort()) {
            System.out.println("Port ouvert avec succès.");
        } else {
            System.out.println("Échec de l'ouverture du port.");
            return;
        }

        comPort.addDataListener(event -> {
            if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

            byte[] newData = new byte[comPort.bytesAvailable()];
            int numRead = comPort.readBytes(newData, newData.length);
            String receivedData = new String(newData, 0, numRead);
            System.out.println("Données reçues : " + receivedData);

            // Parsing du JSON reçu
            try {
                // Conversion des données reçues en objet JSON
                JSONObject jsonReceived = new JSONObject(receivedData);
                int temperature = jsonReceived.getInt("temperature");
                double pression = jsonReceived.getDouble("pression");
                double acceleration = jsonReceived.getDouble("acceleration");
                int vitesse = jsonReceived.getInt("vitesse");
                int altitude = jsonReceived.getInt("altitude");
                long timestamp = jsonReceived.getLong("timestamp");
                int launchId = jsonReceived.getInt("launch_id");

                // Construire la nouvelle requête JSON pour l'API Node.js
                String jsonData = new JSONObject()
                        .put("temperature", temperature)
                        .put("pression", pression)
                        .put("acceleration", acceleration)
                        .put("vitesse", vitesse)
                        .put("altitude", altitude)
                        .put("timestamp", timestamp)
                        .put("launch_id", launchId)
                        .toString();

                // Envoi des données au serveur Node.js via une requête POST
                URL url = new URL("http://localhost:3000/api/data");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Envoi des données JSON dans la requête
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Obtenir la réponse du serveur
                int responseCode = conn.getResponseCode();
                System.out.println("Réponse du serveur : " + responseCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            Thread.sleep(10000); // Ajuste le temps d'attente si nécessaire
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        comPort.closePort();
        System.out.println("Port fermé.");
    }
}
