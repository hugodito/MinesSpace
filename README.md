# MinesSpace
Repository for the MinesSpace website / app / microcontroller project.

## Project Overview

MinesSpace is an integrated system designed to monitor and manage environmental data using a combination of microcontrollers, a backend server, a web application, and a mobile application. The project aims to provide real-time data collection, storage, and visualization.

## Project Structure

- **android-app/**: Contains the Android application built with Kotlin and Jetpack Compose.
- **arduino/**: Contains the Arduino code for the transmitter and receiver modules.
- **backend/**: Node.js backend to handle data from microcontrollers and serve the web application.
- **html/**: Static HTML files for the website.

## How It Works

### Microcontroller Communication
- **Transmitter**: The transmitter module collects sensor data (e.g., temperature, humidity) and sends it via LoRa (Long Range) communication to the receiver module.
- **Receiver**: The receiver module receives the data from the transmitter and sends it to the backend server via WiFi.

### Backend
- **Server**: The backend server (`server.js`) is built with Node.js and Express. It receives data from the receiver module and stores it in a SQLite database.
- **API**: The backend provides RESTful API endpoints to retrieve data for the web and mobile applications. These endpoints allow clients to fetch real-time and historical data.

### Web Application
- **HTML/CSS/JavaScript**: The web application consists of static HTML, CSS, and JavaScript files. It is served by the backend server and provides a user interface to display real-time data, historical data, and information about the project.

### Mobile Application
- **Android App**: The Android application is built with Kotlin and Jetpack Compose. It mirrors the functionality of the web application, providing a mobile-friendly interface to view real-time and historical data.

## Getting Started

### Prerequisites
- Node.js
- npm
- Android Studio (for the Android app)
- Arduino IDE (for the microcontroller code)

### Running the Project

1. **Backend**:
    ```sh
    cd backend
    npm install
    npm start
    ```

2. **Web Application**:
    Open [index.html](http://_vscodecontentref_/1) in a web browser.

3. **Android Application**:
    Open the [android-app](http://_vscodecontentref_/2) folder in Android Studio and run the app on an emulator or physical device.

4. **Microcontroller**:
    Upload the appropriate code to the transmitter and receiver using the Arduino IDE.

## License
This project has been done for the ECOLE DES MINES DE SAINT-ETIENNE ToolBox by :
noemieherbiniere
romainb42
hugodito