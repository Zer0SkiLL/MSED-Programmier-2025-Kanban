# Postman Testing Guide for Kanban Board API

## Prerequisites
1. MongoDB running via Docker Compose
2. Backend application running on `http://localhost:8080`

## Quick Start

### 1. Start MongoDB
```bash
cd modern-kanban
docker-compose up -d
```

### 2. Start Backend
```bash
cd backend
./gradlew bootRun
```

## API Endpoints

### Base URL
```
http://localhost:8080/api
```

---

## Board Endpoints

### 1. Create Board
**POST** `/boards`

```json
{
  "name": "My Project Board"
}
```

**Response (201 Created):**
```json
{
  "id": "65a1234567890abcdef12345",
  "name": "My Project Board",
  "columns": [],
  "createdAt": "1705075200000",
  "updatedAt": "1705075200000"
}
```

### 2. Get All Boards
**GET** `/boards`

**Response (200 OK):**
```json
[
  {
    "id": "65a1234567890abcdef12345",
    "name": "My Project Board",
    "columns": [],
    "createdAt": "1705075200000",
    "updatedAt": "1705075200000"
  }
]
```

### 3. Get Board by ID
**GET** `/boards/{boardId}`

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12345",
  "name": "My Project Board",
  "columns": [
    {
      "id": "65a1234567890abcdef12346",
      "boardId": "65a1234567890abcdef12345",
      "name": "To Do",
      "position": 0,
      "tasks": []
    }
  ],
  "createdAt": "1705075200000",
  "updatedAt": "1705075200000"
}
```

### 4. Update Board
**PUT** `/boards/{boardId}`

```json
{
  "name": "Updated Board Name"
}
```

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12345",
  "name": "Updated Board Name",
  "columns": [],
  "createdAt": "1705075200000",
  "updatedAt": "1705075400000"
}
```

### 5. Delete Board
**DELETE** `/boards/{boardId}`

**Response (204 No Content)**

---

## Column Endpoints

### 1. Create Column
**POST** `/boards/{boardId}/columns`

```json
{
  "title": "To Do"
}
```

**Response (201 Created):**
```json
{
  "id": "65a1234567890abcdef12346",
  "boardId": "65a1234567890abcdef12345",
  "name": "To Do",
  "position": 0,
  "tasks": []
}
```

### 2. Get All Columns in Board
**GET** `/boards/{boardId}/columns`

**Response (200 OK):**
```json
[
  {
    "id": "65a1234567890abcdef12346",
    "boardId": "65a1234567890abcdef12345",
    "name": "To Do",
    "position": 0,
    "tasks": []
  },
  {
    "id": "65a1234567890abcdef12347",
    "boardId": "65a1234567890abcdef12345",
    "name": "In Progress",
    "position": 1,
    "tasks": []
  }
]
```

### 3. Get Column by ID
**GET** `/boards/{boardId}/columns/{columnId}`

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12346",
  "boardId": "65a1234567890abcdef12345",
  "name": "To Do",
  "position": 0,
  "tasks": []
}
```

### 4. Update Column
**PUT** `/boards/{boardId}/columns/{columnId}`

```json
{
  "title": "Updated Column Name"
}
```

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12346",
  "boardId": "65a1234567890abcdef12345",
  "name": "Updated Column Name",
  "position": 0,
  "tasks": []
}
```

### 5. Delete Column
**DELETE** `/boards/{boardId}/columns/{columnId}`

**Response (204 No Content)**

---

## Task Endpoints

### 1. Create Task
**POST** `/boards/{boardId}/columns/{columnId}/tasks`

```json
{
  "title": "Implement authentication",
  "description": "Add JWT authentication to the backend",
  "assignee": "John Doe",
  "priority": "high",
  "dueDate": "1705161600000",
  "tags": ["backend", "security"]
}
```

**Response (201 Created):**
```json
{
  "id": "65a1234567890abcdef12348",
  "columnId": "65a1234567890abcdef12346",
  "title": "Implement authentication",
  "description": "Add JWT authentication to the backend",
  "assignee": "John Doe",
  "priority": "high",
  "tags": ["backend", "security"],
  "dueDate": "1705161600000",
  "createdAt": "1705075200000",
  "updatedAt": "1705075200000"
}
```

### 2. Get All Tasks in Column
**GET** `/boards/{boardId}/columns/{columnId}/tasks`

**Response (200 OK):**
```json
[
  {
    "id": "65a1234567890abcdef12348",
    "columnId": "65a1234567890abcdef12346",
    "title": "Implement authentication",
    "description": "Add JWT authentication to the backend",
    "assignee": "John Doe",
    "priority": "high",
    "tags": ["backend", "security"],
    "dueDate": "1705161600000",
    "createdAt": "1705075200000",
    "updatedAt": "1705075200000"
  }
]
```

### 3. Get Task by ID
**GET** `/boards/{boardId}/columns/{columnId}/tasks/{taskId}`

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12348",
  "columnId": "65a1234567890abcdef12346",
  "title": "Implement authentication",
  "description": "Add JWT authentication to the backend",
  "assignee": "John Doe",
  "priority": "high",
  "tags": ["backend", "security"],
  "dueDate": "1705161600000",
  "createdAt": "1705075200000",
  "updatedAt": "1705075200000"
}
```

### 4. Update Task
**PUT** `/boards/{boardId}/columns/{columnId}/tasks/{taskId}`

```json
{
  "title": "Implement JWT authentication",
  "description": "Add JWT authentication with refresh tokens",
  "assignee": "Jane Smith",
  "priority": "high",
  "dueDate": "1705161600000",
  "tags": ["backend", "security", "jwt"]
}
```

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12348",
  "columnId": "65a1234567890abcdef12346",
  "title": "Implement JWT authentication",
  "description": "Add JWT authentication with refresh tokens",
  "assignee": "Jane Smith",
  "priority": "high",
  "tags": ["backend", "security", "jwt"],
  "dueDate": "1705161600000",
  "createdAt": "1705075200000",
  "updatedAt": "1705075400000"
}
```

### 5. Move Task
**PATCH** `/boards/{boardId}/columns/{columnId}/tasks/{taskId}/move`

```json
{
  "targetColumnId": "65a1234567890abcdef12347",
  "position": 0
}
```

**Response (200 OK):**
```json
{
  "id": "65a1234567890abcdef12348",
  "columnId": "65a1234567890abcdef12347",
  "title": "Implement JWT authentication",
  "description": "Add JWT authentication with refresh tokens",
  "assignee": "Jane Smith",
  "priority": "high",
  "tags": ["backend", "security", "jwt"],
  "dueDate": "1705161600000",
  "createdAt": "1705075200000",
  "updatedAt": "1705075400000"
}
```

### 6. Delete Task
**DELETE** `/boards/{boardId}/columns/{columnId}/tasks/{taskId}`

**Response (204 No Content)**

---

## Testing Workflow

### Complete Workflow Example

1. **Create a Board**
   - POST `/boards` with `{"name": "Sprint Board"}`
   - Save the returned `id` as `{boardId}`

2. **Create Columns**
   - POST `/boards/{boardId}/columns` with `{"title": "To Do"}`
   - Save the returned `id` as `{todoColumnId}`
   - POST `/boards/{boardId}/columns` with `{"title": "In Progress"}`
   - Save the returned `id` as `{inProgressColumnId}`
   - POST `/boards/{boardId}/columns` with `{"title": "Done"}`
   - Save the returned `id` as `{doneColumnId}`

3. **Create Tasks**
   - POST `/boards/{boardId}/columns/{todoColumnId}/tasks`
   ```json
   {
     "title": "Setup Database",
     "description": "Configure MongoDB connection",
     "priority": "high",
     "assignee": "Developer 1",
     "tags": ["database", "setup"]
   }
   ```
   - Save the returned `id` as `{taskId}`

4. **Move Task**
   - PATCH `/boards/{boardId}/columns/{todoColumnId}/tasks/{taskId}/move`
   ```json
   {
     "targetColumnId": "{inProgressColumnId}",
     "position": 0
   }
   ```

5. **Update Task**
   - PUT `/boards/{boardId}/columns/{inProgressColumnId}/tasks/{taskId}`
   ```json
   {
     "title": "Setup Database",
     "description": "MongoDB configured and tested",
     "priority": "high",
     "assignee": "Developer 1",
     "tags": ["database", "setup", "completed"]
   }
   ```

6. **Get All Boards with Data**
   - GET `/boards` (should show all boards with nested columns and tasks)

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-13T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Board name is required",
  "path": "/api/boards"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-13T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Board not found",
  "path": "/api/boards/invalidId"
}
```

---

## Priority Values
- `low`
- `medium` (default)
- `high`
- `urgent`

## Tips for Testing

1. **Use Environment Variables in Postman**
   - Set `baseUrl` = `http://localhost:8080/api`
   - Set `boardId`, `columnId`, `taskId` after creation for easier testing

2. **Test in Order**
   - Create Board → Create Columns → Create Tasks → Move Tasks → Update Tasks

3. **Check MongoDB**
   ```bash
   docker exec -it kanban-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
   use kanban
   db.boards.find()
   db.columns.find()
   db.tasks.find()
   ```

4. **Health Check**
   - GET `http://localhost:8080/actuator/health`

---

## Troubleshooting

### MongoDB Connection Issues
```bash
# Check if MongoDB is running
docker ps | grep kanban-mongodb

# Check MongoDB logs
docker logs kanban-mongodb

# Restart MongoDB
docker-compose restart mongodb
```

### Backend Issues
```bash
# Check backend logs
cd backend
./gradlew bootRun

# Clean and rebuild
./gradlew clean build
```

### Common Errors
- **Connection refused**: MongoDB not started
- **Authentication failed**: Check credentials in `application.properties`
- **404 Not Found**: Wrong endpoint or resource doesn't exist
- **400 Bad Request**: Invalid request body format
