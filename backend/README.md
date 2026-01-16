# Backend Application

This is a minimal **Spring Boot + Kotlin** backend offering REST-APIs for the Kanban board's functionalities.

---

## Requirements

Make sure you have the following installed:

- **Java 21** (required by the Gradle toolchain)
- **Gradle** (optional – the Gradle Wrapper is included)
- **Git** (optional)

You can verify your Java version with:

`java -version`

## How to run

Start up a local MongoDB instance. More instructions [here](../README.md)
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
