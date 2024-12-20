#include <Wire.h>
#include <Adafruit_BMP280.h>
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <SPI.h>
#include <LoRa.h>

// Objets pour capteurs
Adafruit_BMP280 bmp;  // Objet pour BMP280
Adafruit_MPU6050 mpu;    // Objet pour MPU6050

// Pins SPI pour LoRa
#define LORA_SCK 18
#define LORA_MISO 19
#define LORA_MOSI 23
#define LORA_SS 5
#define LORA_RST 14
#define LORA_DIO0 26
#define BAND 915E6  // Fréquence LoRa (modifiez selon votre région)
#define PAS 1000

//initialisation des variables
unsigned long temps1 = 0;

// Initialisation
void setup() {
  
  Serial.begin(9600);
  Wire.begin(21, 22);  // Configurer SDA et SCL pour ESP32

  // Initialiser le MPU6050
  Serial.println("Initialisation du MPU6050...");
  if (!mpu.begin()) {
    Serial.println("Échec de l'initialisation du MPU6050 !");
    while (1);
  }
  Serial.println("MPU6050 initialisé !");

  Serial.println("MPU6050 initialisé !");

  // Initialisation BMP280
  Serial.println("Initialisation du BMP280...");
  if (!bmp.begin(0x76)) {  // Adresse I2C par défaut
    Serial.println("BMP280 non détecté !");
    while (1);
  }

  // Configuration du BMP280
  bmp.setSampling(Adafruit_BMP280::MODE_NORMAL,
                  Adafruit_BMP280::SAMPLING_X2,
                  Adafruit_BMP280::SAMPLING_X16,
                  Adafruit_BMP280::FILTER_X16,
                  Adafruit_BMP280::STANDBY_MS_500);

  // Initialisation LoRa
  Serial.println("Initialisation du LoRa...");
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(BAND)) {
    Serial.println("Échec de l'initialisation LoRa !");
    while (1);
  }
  Serial.println("LoRa initialisé !");
}

void loop() {

  if ((millis()- temps1) >= PAS){
    temps1 = millis();
  // Lire les données du MPU6050
   sensors_event_t accel;
    mpu.getAccelerometerSensor()->getEvent(&accel);

    float accelX = accel.acceleration.x;  // Accélération en m/s²
    float accelY = accel.acceleration.y;
    float accelZ = accel.acceleration.z;


  // Lire les données du BMP280
  float temperature = bmp.readTemperature();
  float pressure = bmp.readPressure() / 100.0;  // En hPa

  // Créer une chaîne de données à envoyer
  String data = "AccelX:" + String(accelX, 2) + 
                ",AccelY:" + String(accelY, 2) + 
                ",AccelZ:" + String(accelZ, 2) +
                ",Temp:" + String(temperature, 2) + 
                ",Pressure:" + String(pressure, 2);

  // Envoyer les données via LoRa
  Serial.println("Envoi des données : " + data);
  LoRa.beginPacket();
  LoRa.print(data);
  LoRa.endPacket();
  }
}
