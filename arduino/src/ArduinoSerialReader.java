import com.fazecast.jSerialComm.SerialPort;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class ArduinoSerialReader {

    public static void main(String[] args) {
        // Remplace "COM3" par le port de ton Arduino
        SerialPort comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(9600); // Assure-toi que cela correspond à la configuration de ton Arduino

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

            // Envoie les données à ton serveur Node.js via une requête POST
            try {
                URL url = new URL("http://localhost:3000/api/data");  // Ton serveur Node.js
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonData = "{\"temperature\": " + receivedData + "}";  // Exemple de donnée, tu peux adapter
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("Réponse du serveur : " + responseCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            Thread.sleep(10000); // Ajuste le temps d'attente selon tes besoins
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        comPort.closePort();
        System.out.println("Port fermé.");
    }
}
