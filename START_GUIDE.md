# Kanban Board - Quick Start Guide

## Prerequisites
- Docker and Docker Compose installed
- Java 21 installed
- Postman (for API testing)

## Step 1: Start MongoDB

```bash
cd modern-kanban
docker-compose up -d
```

Verify MongoDB is running:
```bash
docker ps | grep kanban-mongodb
```

## Step 2: Build the Backend

```bash
cd backend
./gradlew clean build
```

## Step 3: Run the Backend

```bash
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

## Step 4: Test with Postman

Follow the comprehensive testing guide in [`POSTMAN_TESTING_GUIDE.md`](./POSTMAN_TESTING_GUIDE.md)

### Quick Test

1. **Create a Board:**
   ```
   POST http://localhost:8080/api/boards
   Content-Type: application/json
   
   {
     "name": "My First Board"
   }
   ```

2. **Verify it works:**
   ```
   GET http://localhost:8080/api/boards
   ```

## Step 5: Check Health

```
GET http://localhost:8080/actuator/health
```

## Stopping Services

### Stop Backend
Press `Ctrl+C` in the terminal running the backend

### Stop MongoDB
```bash
docker-compose down
```

To remove data volumes:
```bash
docker-compose down -v
```

## Troubleshooting

### MongoDB Connection Failed
1. Check MongoDB is running: `docker ps`
2. Check logs: `docker logs kanban-mongodb`
3. Restart: `docker-compose restart mongodb`

### Port Already in Use
- Backend (8080): Change `server.port` in `backend/src/main/resources/application.properties`
- MongoDB (27017): Change port mapping in `docker-compose.yml`

### Build Errors
```bash
cd backend
./gradlew clean
./gradlew build --refresh-dependencies
```

## Next Steps

1. Review API documentation: [`POSTMAN_TESTING_GUIDE.md`](./POSTMAN_TESTING_GUIDE.md)
2. Test all endpoints with Postman
3. Integrate with the frontend application

## Database Access

Connect to MongoDB directly:
```bash
docker exec -it kanban-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
```

Inside MongoDB shell:
```javascript
use kanban
db.boards.find().pretty()
db.columns.find().pretty()
db.tasks.find().pretty()
```
