#include <Arduino.h>
#include <DHT.h>

// Define sensor type and pin
#define DHTPIN 2      // Pin connected to the sensor
#define DHTTYPE DHT22 // Change to DHT11 if using that model

DHT dht(DHTPIN, DHTTYPE);

int launch_id = 4;

void setup() {
  Serial.begin(9600);
  dht.begin(); // Initialize the sensor
}

void loop() {
  // Read temperature
  float temperature = dht.readTemperature();

  // Check if the reading is valid
  if (isnan(temperature)) {
    Serial.println("Failed to read temperature from DHT sensor!");
    delay(2000);
    return;
  }

  // Create JSON data
  String jsonData = "{";

  jsonData += "\"temperature\":";
  jsonData += String(temperature);
  jsonData += ",";

  jsonData += "\"pression\":";
  jsonData += String(random(900, 1000)); // Replace with actual sensor data if available
  jsonData += ",";

  jsonData += "\"acceleration\":";
  jsonData += String(random(0, 24)); // Replace with actual sensor data if available
  jsonData += ",";

  jsonData += "\"vitesse\":";
  jsonData += String(random(0, 50)); // Replace with actual sensor data if available
  jsonData += ",";

  jsonData += "\"altitude\":";
  jsonData += String(random(0, 2000)); // Replace with actual sensor data if available
  jsonData += ",";

  jsonData += "\"launch_id\":";
  jsonData += String(launch_id);
  jsonData += "}";

  // Send JSON over serial
  Serial.println(jsonData);

  delay(1000); // Wait for 1 second
}
