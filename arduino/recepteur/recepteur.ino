// Code pour la carte Arduino réceptrice
#include <SPI.h>
#include <LoRa.h>

// Configuration des pins pour LoRa
#define LORA_SCK 18
#define LORA_MISO 19
#define LORA_MOSI 23
#define LORA_SS 5
#define LORA_RST 14
#define LORA_DIO0 26
#define BAND 915E6  // Fréquence LoRa (modifiez selon votre région)

void setup() {
  // Initialisation de la communication série
  Serial.begin(9600);
  while (!Serial);
  Serial.println("Initialisation de la carte réceptrice...");

  // Initialisation du module LoRa
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(BAND)) {
    Serial.println("Échec de l'initialisation LoRa !");
    while (1);
  }
  Serial.println("Récepteur LoRa initialisé !");
}

void loop() {
  // Vérification si un paquet est reçu
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    Serial.println("Paquet reçu :");

    // Lire le paquet
    String receivedData = "";
    while (LoRa.available()) {
      receivedData += (char)LoRa.read();
    }

    // Afficher les données reçues sur la console série
    Serial.println(receivedData);
  }

}
