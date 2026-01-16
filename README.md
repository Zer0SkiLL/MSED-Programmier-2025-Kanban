# Kanban Application

A full‑stack Kanban board application with a Kotlin/Spring Boot backend and a Next.js (React) frontend.

This README consolidates the key information to develop, run, and test the app.

- Backend: Kotlin, Spring Boot, MongoDB
- Frontend: Next.js 16, React 19, Tailwind CSS

## Project Structure

```
MSED-Programmier-2025-Kanban/
├─ backend/                 # Spring Boot (Kotlin) service
│  ├─ build.gradle.kts      # Gradle Kotlin DSL build
│  ├─ gradlew / gradlew.bat # Gradle Wrapper
│  └─ src/                  # Kotlin code and resources
├─ frontend/                # Next.js app
│  ├─ package.json          # npm scripts
│  └─ app/ ...              # Next.js App Router (pages/components)
└─ README.md                # This file
```

## Tech Stack and Entry Points

- Backend
    - Language: Kotlin (JVM), Java toolchain 21
    - Framework: Spring Boot 4 (Web MVC, Validation, Actuator, Spring Data MongoDB)
    - Build: Gradle (Wrapper included)
    - Entry point:
      backend/src/main/kotlin/.../[BackendApplication.kt](backend/src/main/kotlin/org/widmerkillenberger/backend/BackendApplication.kt)
      (Spring Boot application)
    - Default port: 8080
- Frontend
    - Framework: Next.js 16 (React 19)
    - Styling: Tailwind CSS
    - Package manager: npm (package-lock.json present)
    - Default dev server port: 3000

## Requirements

- Java 21 (required by backend Gradle toolchain)
- Node.js (LTS recommended; Next.js 16 typically requires Node 18+ or 20+)
- npm (comes with Node)
- MongoDB (default configuration targets an instance running on localhost:27017) — used by backend via Spring Data MongoDB
- Git (optional)

> Tip (Windows): Use PowerShell commands shown with `./gradlew` or `\.\gradlew` depending on shell; examples below use
> PowerShell style.

## Setup

1) Clone the repository

- git clone <repo-url>
- cd MSED-Programmier-2025-Kanban

2) Backend dependencies

- cd backend
- .\gradlew --version (verifies wrapper and Java 21 availability)
- .\gradlew build

3) Frontend dependencies

- cd ..\frontend
- npm install

4) MongoDB

- Install Docker Desktop locally
- Run the following command to spin up a container running a MongoDB inside
    - `docker run -d --name mongodb -p 27017:27017 -v mongo_data:/data/db mongo:7.0`
- You should see a mongodb container running when you open Docker Desktop. You can start and stop it now using Docker Desktop.

## Running the Application

You can run backend and frontend in two terminals.

- Ensure a MongoDB instance is running on localhost:27017. (see instructions above)

- Start backend (terminal 1)
    - cd backend
    - .\gradlew bootRun
    - Health check (once started): http://localhost:8080/actuator/health

- Start frontend (terminal 2)
    - cd frontend
    - npm run dev
    - Open: http://localhost:3000
