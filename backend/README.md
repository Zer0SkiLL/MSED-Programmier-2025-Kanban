# Backend Application

This is a minimal **Spring Boot + Kotlin** backend offering REST-APIs for the Kanban board's functionalities.  
At the moment, it only contains the base `BackendApplication.kt` entry point and no additional functionality.

---

## Requirements

Make sure you have the following installed:

- **Java 21** (required by the Gradle toolchain)
- **Docker Desktop** (required to run the MongoDB container)
- **Gradle** (optional – the Gradle Wrapper is included)
- **Git** (optional)

You can verify your Java version with:

`java -version`

## How to run
### Spin up the Database
Install Docker Desktop on your machine. Follow install guides from docker or ask AI for help.  
Once installed, run the following command:

`docker run -d --name mongodb -p 27017:27017 -v mongo_data:/data/db mongo:7.0`  

This will spin up a MongoDB container on your machine.  

### Run the Application
Navigate to the backend folder in your terminal. Run the following command:  
`.\gradlew bootRun`  
This should start the application and you can check the application using the actuator endpoints.

## Actuator Endpoints

This project includes **Spring Boot Actuator** for basic health and application insights.

### Base URL
By default, the application runs on:

- `http://localhost:8080`

### Common endpoints

- **Health**
    - `GET /actuator/health`
    - Shows whether the app is up (expected: `{"status":"UP"}`)

- **Liveness / Readiness** (if enabled)
    - `GET /actuator/health/liveness`
    - `GET /actuator/health/readiness`
