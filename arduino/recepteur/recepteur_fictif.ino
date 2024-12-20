#include <Arduino.h>
#include <Wire.h>
#include <SPI.h>


int nombre = 24;
int launch_id = 4;

void setup() {
  Serial.begin(9600);
  
}

void loop() {
  // Envoi des données sous forme de JSON

  String jsonData = "{";
  jsonData += "\"id\":" ;
  jsonData += nombre;;
  nombre = nombre +1;
  jsonData += ", ";

  jsonData += "\"temperature\":" ;
  jsonData += String(random(15,25));
  jsonData += ", ";

  jsonData += "\"pression\":" ;
  jsonData += String(random(900,1000));
  jsonData += ", ";

  jsonData += "\"accélération\":" ;
  jsonData += String(random(0,24));
  jsonData += ", ";

  jsonData += "\"vitesse\":" ;
  jsonData += String(random(0,50));
  jsonData += ", ";

  jsonData += "\"altitude\":" ;
  jsonData += String(random(0,2000));
  jsonData += ", ";

  jsonData += "\"timestamp\": " + String(millis()) + ", ";
  jsonData += "\"launch_id\": " + String(launch_id);
  jsonData += "}";

  Serial.println("Données envoyées :");
  Serial.println(jsonData);
  delay(1000);
}