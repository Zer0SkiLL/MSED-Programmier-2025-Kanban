# API Endpoints Reference

## Base URL
```
http://localhost:8080
```

## Boards

### Get All Boards
```http
GET /api/boards
```

**Response:**
```json
[
  {
    "id": "1",
    "name": "Project Alpha",
    "columns": [...],
    "workflowRules": [...],
    "activityLog": [...]
  }
]
```

### Get Board by ID
```http
GET /api/boards/{id}
```

**Response:**
```json
{
  "id": "1",
  "name": "Project Alpha",
  "columns": [...],
  "workflowRules": [...],
  "activityLog": [...]
}
```

### Create Board
```http
POST /api/boards
Content-Type: application/json

{
  "name": "New Project"
}
```

**Response:**
```json
{
  "id": "2",
  "name": "New Project",
  "columns": [
    {
      "id": "todo",
      "title": "To Do",
      "tasks": []
    },
    {
      "id": "in-progress",
      "title": "In Progress",
      "tasks": []
    },
    {
      "id": "review",
      "title": "Review",
      "tasks": []
    },
    {
      "id": "done",
      "title": "Done",
      "tasks": []
    }
  ],
  "workflowRules": [
    {
      "fromColumnId": "todo",
      "toColumnIds": ["in-progress"]
    },
    {
      "fromColumnId": "in-progress",
      "toColumnIds": ["review", "todo"]
    },
    {
      "fromColumnId": "review",
      "toColumnIds": ["done", "in-progress"]
    },
    {
      "fromColumnId": "done",
      "toColumnIds": ["in-progress"]
    }
  ],
  "activityLog": []
}
```

### Update Board
```http
PUT /api/boards/{id}
Content-Type: application/json

{
  "name": "Updated Project Name"
}
```

**Response:**
```json
{
  "id": "1",
  "name": "Updated Project Name",
  "columns": [...],
  "workflowRules": [...],
  "activityLog": [...]
}
```

### Delete Board
```http
DELETE /api/boards/{id}
```

**Response:** `204 No Content`

---

## Columns

### Get All Columns for a Board
```http
GET /api/boards/{boardId}/columns
```

**Response:**
```json
[
  {
    "id": "todo",
    "title": "To Do",
    "tasks": [...]
  },
  {
    "id": "in-progress",
    "title": "In Progress",
    "tasks": [...]
  }
]
```

### Create Column
```http
POST /api/boards/{boardId}/columns
Content-Type: application/json

{
  "title": "New Column"
}
```

**Response:**
```json
{
  "id": "3",
  "title": "New Column",
  "tasks": []
}
```

### Update Column
```http
PUT /api/boards/{boardId}/columns/{columnId}
Content-Type: application/json

{
  "title": "Updated Column Name"
}
```

**Response:**
```json
{
  "id": "3",
  "title": "Updated Column Name",
  "tasks": [...]
}
```

### Delete Column
```http
DELETE /api/boards/{boardId}/columns/{columnId}
```

**Response:** `204 No Content`

---

## Tasks

### Get All Tasks for a Column
```http
GET /api/boards/{boardId}/columns/{columnId}/tasks
```

**Response:**
```json
[
  {
    "id": "1",
    "title": "Design new landing page",
    "description": "Create wireframes and mockups",
    "priority": "high",
    "assignee": "John Doe",
    "dueDate": "2025-01-15",
    "tags": ["design", "frontend"],
    "activityLog": [...]
  }
]
```

### Create Task
```http
POST /api/boards/{boardId}/columns/{columnId}/tasks
Content-Type: application/json

{
  "title": "New Task",
  "description": "Task description",
  "priority": "medium",
  "assignee": "Jane Smith",
  "dueDate": "2025-01-20",
  "tags": ["backend", "api"]
}
```

**Response:**
```json
{
  "id": "6",
  "title": "New Task",
  "description": "Task description",
  "priority": "medium",
  "assignee": "Jane Smith",
  "dueDate": "2025-01-20",
  "tags": ["backend", "api"],
  "activityLog": [
    {
      "id": "1",
      "taskId": "6",
      "boardId": "1",
      "action": "created",
      "description": "Task created in \"To Do\"",
      "timestamp": "2025-01-09T12:00:00Z",
      "user": "Current User"
    }
  ]
}
```

### Update Task
```http
PUT /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}
Content-Type: application/json

{
  "title": "Updated Task Title",
  "description": "Updated description",
  "priority": "high",
  "assignee": "Bob Johnson",
  "dueDate": "2025-01-25",
  "tags": ["backend", "api", "urgent"]
}
```

**Response:**
```json
{
  "id": "6",
  "title": "Updated Task Title",
  "description": "Updated description",
  "priority": "high",
  "assignee": "Bob Johnson",
  "dueDate": "2025-01-25",
  "tags": ["backend", "api", "urgent"],
  "activityLog": [...]
}
```

### Delete Task
```http
DELETE /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}
```

**Response:** `204 No Content`

### Move Task Between Columns
```http
PATCH /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}/move
Content-Type: application/json

{
  "targetColumnId": "in-progress",
  "position": 0
}
```

**Response:**
```json
{
  "id": "6",
  "title": "Task Title",
  "description": "Task description",
  "priority": "high",
  "assignee": "Bob Johnson",
  "dueDate": "2025-01-25",
  "tags": ["backend", "api", "urgent"],
  "activityLog": [
    ...,
    {
      "id": "2",
      "taskId": "6",
      "boardId": "1",
      "action": "moved",
      "description": "Moved from \"To Do\" to \"In Progress\"",
      "timestamp": "2025-01-09T12:30:00Z",
      "user": "Current User"
    }
  ]
}
```

---

## Activity Logs

### Get Activity Logs for a Board
```http
GET /api/boards/{boardId}/activity-logs
```

**Response:**
```json
[
  {
    "id": "1",
    "taskId": "6",
    "boardId": "1",
    "action": "created",
    "description": "Task created in \"To Do\"",
    "timestamp": "2025-01-09T12:00:00Z",
    "user": "Current User"
  },
  {
    "id": "2",
    "taskId": "6",
    "boardId": "1",
    "action": "moved",
    "description": "Moved from \"To Do\" to \"In Progress\"",
    "timestamp": "2025-01-09T12:30:00Z",
    "user": "Current User"
  }
]
```

### Get Activity Logs for a Task
```http
GET /api/boards/{boardId}/tasks/{taskId}/activity-logs
```

**Response:**
```json
[
  {
    "id": "1",
    "taskId": "6",
    "boardId": "1",
    "action": "created",
    "description": "Task created in \"To Do\"",
    "timestamp": "2025-01-09T12:00:00Z",
    "user": "Current User"
  },
  {
    "id": "2",
    "taskId": "6",
    "boardId": "1",
    "action": "updated",
    "description": "Task updated",
    "timestamp": "2025-01-09T12:15:00Z",
    "user": "Current User"
  },
  {
    "id": "3",
    "taskId": "6",
    "boardId": "1",
    "action": "moved",
    "description": "Moved from \"To Do\" to \"In Progress\"",
    "timestamp": "2025-01-09T12:30:00Z",
    "user": "Current User"
  }
]
```

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-01-09T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: Title is required",
  "path": "/api/boards"
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-01-09T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Board not found with id: 999",
  "path": "/api/boards/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-01-09T12:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/boards"
}
```

---

## Workflow Rules Validation

When moving a task between columns, the API will validate against the board's workflow rules:

```json
{
  "fromColumnId": "todo",
  "toColumnIds": ["in-progress"]
}
```

This means tasks in "todo" can only be moved to "in-progress". Attempting to move to another column will result in a 400 Bad Request error.

### Example Error Response
```json
{
  "timestamp": "2025-01-09T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot move task from 'todo' to 'done'. Allowed columns: in-progress",
  "path": "/api/boards/1/columns/todo/tasks/1/move"
}
```
