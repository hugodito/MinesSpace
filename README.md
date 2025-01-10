# MinesSpace
Repository for the MinesSpace website / app / microcontroller project.

## Project Overview

MinesSpace is an integrated system designed to monitor and manage environmental data using a combination of microcontrollers, a backend server, a web application, and a mobile application. The project aims to provide real-time data collection, storage, and visualization during the launch of experimental rockets for the association Mines Space. Since we cannot test this project in real-life condition, the only sensor used will be the temperature sensor and the rest of the data will be fake, generated randomly.

## Project Structure

- **android-app/**: Contains the Android application built with Kotlin and Jetpack Compose. Gives access to the previous launches and their data.
- **arduino/**: Contains the Arduino code for the receiver board (Adafruit ESP32 Feather) in order to send the data.
- **backend/**: Node.js backend to handle data from microcontrollers and serve the web application as well as the app.
- **html/**: Static HTML files for the website's frontend, CSS for the style as well as Javascript files to rule its pages and access to the API.

## How It Works

### Microcontroller Communication
- **Transmitter**: The transmitter module collects sensor data (temperatur, altitude, pressure, etc) and sends it via LoRa (Long Range radio) communication to the receiver module.
- **Receiver**: The receiver module receives the data from the transmitter and sends it to the backend server USB.
Actually, we didn't use a transmitter, only the receiver and faked the aquisition of data with a temperature sensor and randomly generated values for, for example, altitude, pressure, etc.

### Backend
- **Server**: The backend server (`server.js`) is built with Node.js and Express. It receives data from the receiver module and stores it in a SQLite database.
- **API**: The backend provides RESTful API endpoints to retrieve data for the web and mobile applications. These endpoints allow clients to fetch real-time and historical data.

### Web Application
- **HTML/CSS/JavaScript**: The web application consists of static HTML, CSS, and JavaScript files. It is served by the backend server and provides a user interface to display historical projetcs, an introduction to the association, a login page for administrators, a live page for when we simulate a launch and a contact page.

### Mobile Application
- **Android App**: The Android application is built with Kotlin and Jetpack Compose. It provides access to the past projects as soon as they are saved in the database and displays a data table.

## Getting Started

### Prerequisites
- Node.js
- npm
- Android Studio (for the Android app)
- Arduino IDE (for the microcontroller code)
- An Adafruit ESP32
- Wires
- A temparature sensor DHT22

### Running the Project

1. **Backend**:
    ```sh
    cd backend
    npm install
    npm start
    ```
2. **Microcontroller**:
    Connect the temperature sensor on the PIN 12 of the ESP32.
    Connect the ESP32 to the computer via USB.
    Open ArduinoSerialReader.java in arduino/src/ and change "COM8" (line 14) to the port where the ESP32 is connected.
    Open the file "recepteur_fictif.ino" in arduino/recepteur_fictif/ and upload the code in the board.

4. **Web Application**:
    Open [index.html](localhost:3000) in a web browser.

5. **Android Application**:
    Open the [android-app] folder in Android Studio and run the app on an emulator or physical device.

### Accessing the different properties

On the website :

When you first start the server, there is already some data in the database that you can consult in "Nos projets" (localhost:3000/projects.html). You can also access to a short introduction of the association in "A propos" (localhost:3000/about.html) and a contact form in "Contact" (localhost:3000/contact.html). You can click on the red "Live" button to access the data in live (localhost:3000/live.html), if the data isn't changing, it means that there is no live at the moment, therefore the server is not saving the data sent by the board. 

To start a live you need to connect to the website, click on "Login" and fill the following info : "admin" for the username and "admin123" for the password. You should receive a notification saying that you are successfully connected, you can then go back to the Live page where a green button saying "Enregistrer les données" should have appeared. If you click on it, the server will begin to display the data and save it into the api (localhost:3000/api/data). When you click on "Arrêter l'enregistrement", the api will stop receiving data and a new project will have been created and will appear in "Nos projets", featuring the data sent during the live. 

On the app :

All you need is to start the app in Android Studio (on a physical device or emulator), once the app is launched, the home page will appear, click on the red button "Accéder aux lancers". A list of the different launches saved in the api will appear, with the data. You can click on one of them and a data table of the launch will appear.

### What could be implemented in the future

- On the app : display the data with graphs instead of a table that isn't easy to read and add a Live page
- On the website : a fonctionnality to delete some lines of data, or a whole project, from the website when logged as an admin (it has already been implemented in server.js, it just needs to be developped in projects.js (in html/js/) and in projects.html) and a better display of the projects' graphs

## License
- This project has been done for the ECOLE DES MINES DE SAINT-ETIENNE ToolBox PWME by :
- noemieherbiniere
- romainb42
- hugodito
