# Frontend-Backend Integration Guide

This guide explains how to connect and run the frontend (Next.js) with the backend (Spring Boot) for the Kanban application.

## Overview

The frontend and backend are now fully integrated:

- **Frontend**: Next.js application running on port 3000
- **Backend**: Spring Boot application running on port 8080
- **API Communication**: Frontend makes HTTP requests to backend REST API
- **CORS**: Backend is configured to allow requests from frontend

## Prerequisites

Before starting, ensure you have:

1. **MongoDB** installed and running on port 27017
2. **Java 17+** installed
3. **Node.js 18+** and **npm** installed
4. **Gradle** (for backend builds)

## Step 1: Start MongoDB

The backend requires MongoDB to store data.

### Option A: Using Docker (Recommended)

```bash
docker run -d -p 27017:27017 --name kanban-mongo mongo:latest
```

### Option B: Local Installation

If you have MongoDB installed locally:

```bash
# macOS (using Homebrew)
brew services start mongodb-community

# Linux
sudo systemctl start mongod

# Windows
net start MongoDB
```

Verify MongoDB is running:

```bash
# Check if MongoDB is listening on port 27017
lsof -i :27017
```

## Step 2: Start the Backend

Navigate to the backend directory and start the Spring Boot application.

```bash
cd modern-kanban/backend

# Option A: Using Gradle wrapper (recommended)
./gradlew bootRun

# Option B: Using installed Gradle
gradle bootRun

# Option C: Build and run JAR
./gradlew build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

The backend will start on `http://localhost:8080`

**Expected Output:**
```
Started BackendApplication in X.XXX seconds
```

**Verify Backend:**
```bash
curl http://localhost:8080/api/boards
```

You should see an empty array `[]` or existing boards.

## Step 3: Start the Frontend

Navigate to the frontend directory and start the Next.js development server.

```bash
cd modern-kanban/frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:3000`

**Expected Output:**
```
ready - started server on 0.0.0.0:3000, url: http://localhost:3000
```

## Step 4: Access the Application

Open your browser and navigate to:

```
http://localhost:3000
```

You should see the Kanban board interface.

## Testing the Integration

### 1. Create a Board

1. Click the board selector dropdown in the header
2. Click "Create New Board"
3. Enter a board name (e.g., "My First Board")
4. Click "Add Board"

**Expected Result:**
- A new board is created in MongoDB
- The board appears in the dropdown selector
- The board is displayed with default columns (To Do, In Progress, Review, Done)

**Verify in Backend:**
```bash
curl http://localhost:8080/api/boards
```

### 2. Add a Task

1. Click "Add Task" button in any column
2. Fill in the task details:
   - Title: "Test Task"
   - Description: "This is a test task"
   - Priority: Medium
   - Assignee: "John Doe"
   - Due Date: Select a date
   - Tags: "test, integration"
3. Click "Add Task"

**Expected Result:**
- The task appears in the column
- The task count badge updates
- The task is saved in MongoDB

**Verify in Backend:**
```bash
curl http://localhost:8080/api/boards/{boardId}/columns/{columnId}/tasks
```

### 3. Move a Task

1. Drag a task from one column to another
2. Drop it in the target column

**Expected Result:**
- The task moves to the new column
- An activity log entry is created
- The move is persisted in MongoDB

**Verify in Backend:**
```bash
curl http://localhost:8080/api/boards/{boardId}/activity-logs
```

### 4. Edit a Task

1. Click the three-dot menu on a task card
2. Click "Edit Task"
3. Modify task details
4. Click "Save Changes"

**Expected Result:**
- The task updates in the UI
- Changes are persisted in MongoDB
- An activity log entry is created

### 5. Delete a Task

1. Click the three-dot menu on a task card
2. Click "Delete Task"

**Expected Result:**
- The task is removed from the UI
- The task is deleted from MongoDB

### 6. Add/Edit/Delete Columns

1. Click "Add Column" to create a new column
2. Click the three-dot menu on a column header
3. Select "Edit Column" or "Delete Column"

**Expected Result:**
- Columns are created, updated, or deleted
- Changes are persisted in MongoDB

## Troubleshooting

### Backend Won't Start

**Issue:** Connection refused to MongoDB

**Solution:**
1. Verify MongoDB is running: `lsof -i :27017`
2. Check MongoDB logs for errors
3. Ensure MongoDB URI in `application.properties` is correct:
   ```
   spring.data.mongodb.uri=mongodb://localhost:27017/kanban
   ```

### Frontend Can't Connect to Backend

**Issue:** CORS errors or connection refused

**Solution:**
1. Verify backend is running on port 8080
2. Check CORS configuration in [`CorsConfig.kt`](backend/src/main/kotlin/org/widmerkillenberger/backend/config/CorsConfig.kt)
3. Verify API URL in [`.env.local`](frontend/.env.local):
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080
   ```
4. Check browser console for specific error messages

### Tasks Not Persisting

**Issue:** Tasks appear in UI but disappear on refresh

**Solution:**
1. Check backend logs for errors
2. Verify MongoDB connection is stable
3. Check browser network tab for failed API requests
4. Ensure API calls are successful (status 200/201)

### Drag and Drop Not Working

**Issue:** Tasks can't be moved between columns

**Solution:**
1. Check browser console for JavaScript errors
2. Verify workflow rules allow the move
3. Check that `tasksApi.move()` is being called in [`kanban-board.tsx`](components/kanban-board.tsx)

### Empty State Displayed

**Issue:** "No Boards Yet" message appears

**Solution:**
1. Create a board using the "Create New Board" option
2. Verify board creation succeeded in backend logs
3. Refresh the page

## API Endpoints Reference

For a complete list of available API endpoints, see [`API_ENDPOINTS.md`](API_ENDPOINTS.md).

Key endpoints:
- `GET /api/boards` - Get all boards
- `POST /api/boards` - Create a board
- `GET /api/boards/{id}` - Get a specific board
- `PUT /api/boards/{id}` - Update a board
- `DELETE /api/boards/{id}` - Delete a board
- `POST /api/boards/{boardId}/columns` - Create a column
- `PUT /api/boards/{boardId}/columns/{columnId}` - Update a column
- `DELETE /api/boards/{boardId}/columns/{columnId}` - Delete a column
- `POST /api/boards/{boardId}/columns/{columnId}/tasks` - Create a task
- `PUT /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}` - Update a task
- `DELETE /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}` - Delete a task
- `PATCH /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}/move` - Move a task

## Development Tips

### Hot Reload

- **Frontend**: Next.js supports hot reload - changes appear automatically
- **Backend**: Spring Boot DevTools supports hot reload for most changes

### Debugging

**Frontend:**
- Use browser DevTools (F12)
- Check Console for errors
- Check Network tab for API requests

**Backend:**
- Check terminal output for logs
- Set logging level in `application.properties`:
  ```
  logging.level.org.widmerkillenberger.backend=DEBUG
  ```

### Testing API Directly

Use curl or Postman to test API endpoints directly:

```bash
# Create a board
curl -X POST http://localhost:8080/api/boards \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Board"}'

# Get all boards
curl http://localhost:8080/api/boards

# Get specific board
curl http://localhost:8080/api/boards/{boardId}
```

## File Structure

### Backend Integration Files

- [`CorsConfig.kt`](backend/src/main/kotlin/org/widmerkillenberger/backend/config/CorsConfig.kt) - CORS configuration
- [`BoardController.kt`](backend/src/main/kotlin/org/widmerkillenberger/backend/controller/BoardController.kt) - Board API endpoints
- [`ColumnController.kt`](backend/src/main/kotlin/org/widmerkillenberger/backend/controller/ColumnController.kt) - Column API endpoints
- [`TaskController.kt`](backend/src/main/kotlin/org/widmerkillenberger/backend/controller/TaskController.kt) - Task API endpoints

### Frontend Integration Files

- [`lib/api.ts`](frontend/lib/api.ts) - API client service
- [`.env.local`](frontend/.env.local) - Environment configuration
- [`app/page.tsx`](frontend/app/page.tsx) - Main page with board fetching
- [`components/kanban-board.tsx`](frontend/components/kanban-board.tsx) - Board component with API calls
- [`components/kanban-column.tsx`](frontend/components/kanban-column.tsx) - Column component with API calls

## Next Steps

After successful integration:

1. **Add Authentication**: Implement user authentication and authorization
2. **Add Real-time Updates**: Use WebSockets for real-time collaboration
3. **Add File Attachments**: Allow users to attach files to tasks
4. **Add Search & Filter**: Implement search and filtering capabilities
5. **Add Export**: Export boards to various formats (PDF, CSV, etc.)

## Support

For issues or questions:
1. Check the [README.md](README.md) for general project information
2. Review [API_ENDPOINTS.md](API_ENDPOINTS.md) for API documentation
3. Check browser console and backend logs for error messages
4. Verify all prerequisites are met and services are running
