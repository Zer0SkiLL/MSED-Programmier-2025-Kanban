# Quick Start Guide

## Prerequisites

### Backend
- Java 21 or higher
- MongoDB (running locally or accessible via connection string)
- Gradle (included in project)

### Frontend
- Node.js 18 or higher
- npm or yarn

## Setup Instructions

### 1. Start MongoDB

**Option A: Using Docker (Recommended)**
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

**Option B: Local Installation**
- Download and install MongoDB from https://www.mongodb.com/try/download/community
- Start MongoDB service:
  - macOS: `brew services start mongodb-community`
  - Linux: `sudo systemctl start mongod`
  - Windows: Start MongoDB service from Services

### 2. Backend Setup

```bash
cd modern-kanban/backend

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd modern-kanban/frontend

# Install dependencies
npm install

# Create environment file
echo "NEXT_PUBLIC_API_BASE_URL=http://localhost:8080" > .env.local

# Start development server
npm run dev
```

The frontend will start on `http://localhost:3000`

## Development Workflow

### Backend Development

1. **Add MongoDB dependency** (already in build.gradle.kts, just uncomment):
   ```kotlin
   implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
   ```

2. **Configure application.properties**:
   ```properties
   spring.application.name=backend
   server.port=8080
   spring.data.mongodb.uri=mongodb://localhost:27017/kanban
   ```

3. **Create entities** in `src/main/kotlin/org/widmerkillenberger/backend/model/entity/`

4. **Create repositories** in `src/main/kotlin/org/widmerkillenberger/backend/repository/`

5. **Create services** in `src/main/kotlin/org/widmerkillenberger/backend/service/`

6. **Create controllers** in `src/main/kotlin/org/widmerkillenberger/backend/controller/`

7. **Test endpoints** using Postman, curl, or the frontend

### Frontend Development

1. **Create API client** in `lib/api/client.ts`

2. **Create API services** in `lib/api/`:
   - `boards.ts`
   - `columns.ts`
   - `tasks.ts`
   - `activity-logs.ts`

3. **Update components** to use API services instead of mock data

4. **Add loading states and error handling**

5. **Test integration** with backend

## Testing the API

### Using curl

**Get all boards:**
```bash
curl http://localhost:8080/api/boards
```

**Create a board:**
```bash
curl -X POST http://localhost:8080/api/boards \
  -H "Content-Type: application/json" \
  -d '{"name":"My First Board"}'
```

**Get a specific board:**
```bash
curl http://localhost:8080/api/boards/{board-id}
```

### Using Postman

1. Import the API endpoints from `API_ENDPOINTS.md`
2. Set base URL to `http://localhost:8080`
3. Test each endpoint

## Common Issues

### MongoDB Connection Failed
- Ensure MongoDB is running: `docker ps` or `brew services list`
- Check connection string in `application.properties`
- Verify MongoDB is accessible on port 27017

### CORS Errors
- Ensure CORS is configured in the backend
- Check that the frontend URL is allowed in CORS configuration

### Frontend Not Connecting to Backend
- Verify backend is running on port 8080
- Check `NEXT_PUBLIC_API_BASE_URL` in `.env.local`
- Ensure no firewall is blocking the connection

### Build Errors
- Clean and rebuild: `./gradlew clean build`
- Check Java version: `java -version` (should be 21+)
- Verify Gradle wrapper is executable: `chmod +x gradlew`

## Project Structure Overview

```
modern-kanban/
├── backend/                    # Spring Boot + Kotlin
│   ├── src/main/kotlin/
│   │   └── org/widmerkillenberger/backend/
│   │       ├── config/        # CORS, security config
│   │       ├── controller/    # REST endpoints
│   │       ├── service/       # Business logic
│   │       ├── repository/    # Data access
│   │       └── model/         # Entities & DTOs
│   └── src/main/resources/
│       └── application.properties
│
├── frontend/                   # Next.js + React + TypeScript
│   ├── app/                   # Next.js app directory
│   ├── components/            # React components
│   ├── lib/
│   │   ├── api/              # API services
│   │   └── utils.ts          # Utility functions
│   └── .env.local            # Environment variables
│
├── DEVELOPMENT_PLAN.md        # Detailed development plan
├── API_ENDPOINTS.md           # API documentation
└── QUICK_START.md            # This file
```

## Next Steps

1. ✅ Review the [DEVELOPMENT_PLAN.md](./DEVELOPMENT_PLAN.md) for detailed tasks
2. ✅ Review the [API_ENDPOINTS.md](./API_ENDPOINTS.md) for API specifications
3. ✅ Start MongoDB
4. ✅ Begin backend development (Phase 1-4)
5. ✅ Test backend endpoints
6. ✅ Begin frontend API integration (Phase 5-7)
7. ✅ Test end-to-end functionality
8. ✅ Polish and document

## Useful Commands

### Backend
```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run tests
./gradlew test

# Clean
./gradlew clean
```

### Frontend
```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Start production server
npm start

# Run linter
npm run lint
```

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev)
- [MongoDB Documentation](https://www.mongodb.com/docs/)

## Support

If you encounter issues:
1. Check the error messages in the terminal
2. Review the logs in the backend console
3. Check the browser console for frontend errors
4. Verify all services are running (MongoDB, backend, frontend)
5. Consult the documentation files in this project
