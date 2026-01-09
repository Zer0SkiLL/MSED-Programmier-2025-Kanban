# Kanban Web App Development Plan

## Overview
This document outlines the development plan for connecting the Next.js frontend with a Spring Boot backend for a fully functional kanban board application.

## Technology Stack

### Frontend
- **Framework**: Next.js 16.0.10 with React 19.2.0
- **Language**: TypeScript
- **UI Components**: Radix UI with shadcn/ui
- **Drag & Drop**: @dnd-kit
- **Styling**: Tailwind CSS 4.1.9

### Backend
- **Framework**: Spring Boot 4.0.1
- **Language**: Kotlin 2.2.21
- **Database**: MongoDB
- **Build Tool**: Gradle with Kotlin DSL

## Data Models

### Task
```typescript
{
  id: string
  title: string
  description?: string
  priority?: "low" | "medium" | "high"
  assignee?: string
  dueDate?: string
  tags?: string[]
  activityLog?: ActivityLog[]
}
```

### Column
```typescript
{
  id: string
  title: string
  tasks: Task[]
}
```

### Board
```typescript
{
  id: string
  name: string
  columns: Column[]
  workflowRules?: WorkflowRule[]
  activityLog?: ActivityLog[]
}
```

### WorkflowRule
```typescript
{
  fromColumnId: string
  toColumnIds: string[]
}
```

### ActivityLog
```typescript
{
  id: string
  taskId?: string
  boardId?: string
  action: string
  description: string
  timestamp: string
  user: string
}
```

## API Endpoints Design

### Boards
- `GET /api/boards` - Get all boards
- `GET /api/boards/{id}` - Get board by ID
- `POST /api/boards` - Create new board
- `PUT /api/boards/{id}` - Update board
- `DELETE /api/boards/{id}` - Delete board

### Columns
- `GET /api/boards/{boardId}/columns` - Get all columns for a board
- `POST /api/boards/{boardId}/columns` - Create column
- `PUT /api/boards/{boardId}/columns/{columnId}` - Update column
- `DELETE /api/boards/{boardId}/columns/{columnId}` - Delete column

### Tasks
- `GET /api/boards/{boardId}/columns/{columnId}/tasks` - Get all tasks for a column
- `POST /api/boards/{boardId}/columns/{columnId}/tasks` - Create task
- `PUT /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}` - Update task
- `DELETE /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}` - Delete task
- `PATCH /api/boards/{boardId}/columns/{columnId}/tasks/{taskId}/move` - Move task between columns

### Activity Logs
- `GET /api/boards/{boardId}/activity-logs` - Get activity logs for board
- `GET /api/boards/{boardId}/tasks/{taskId}/activity-logs` - Get activity logs for task

## Development Phases

### Phase 1: Backend Setup & Configuration
1. Configure MongoDB connection in application.properties
2. Uncomment and add MongoDB dependency in build.gradle.kts
3. Add validation and CORS dependencies
4. Configure CORS to allow frontend requests
5. Set server port (default: 8080)

### Phase 2: Backend Data Layer
1. Create entity classes for Task, Column, Board, ActivityLog, WorkflowRule
2. Create DTOs for request/response
3. Create repository interfaces extending MongoRepository
4. Add custom query methods if needed

### Phase 3: Backend Service Layer
1. Create BoardService with CRUD operations
2. Create ColumnService with CRUD operations
3. Create TaskService with CRUD operations
4. Create ActivityLogService for logging
5. Implement workflow rules validation logic

### Phase 4: Backend REST Controllers
1. Create BoardController with all endpoints
2. Create ColumnController with all endpoints
3. Create TaskController with all endpoints
4. Create ActivityLogController with all endpoints
5. Add request validation
6. Add error handling

### Phase 5: Frontend API Client
1. Create API client configuration with base URL
2. Create TypeScript types matching backend DTOs
3. Create error handling utilities
4. Create loading state management utilities

### Phase 6: Frontend API Services
1. Create boards API service
2. Create columns API service
3. Create tasks API service
4. Create activity logs API service

### Phase 7: Frontend Integration
1. Update page.tsx to use API calls
2. Update all components to use API services
3. Add loading states
4. Add error handling
5. Implement optimistic updates

### Phase 8: Testing & Polish
1. Test all CRUD operations
2. Test error scenarios
3. Test concurrent operations
4. Verify data persistence
5. Add loading indicators
6. Add toast notifications

### Phase 9: Documentation
1. Document API endpoints
2. Create backend README
3. Update frontend README
4. Add deployment instructions

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Next.js Frontend                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐   │
│  │   UI     │  │  State   │  │   API Service Layer  │   │
│  │Components│  │Management│  │  (boards, columns,   │   │
│  └────┬─────┘  └────┬─────┘  │   tasks, activity)   │   │
│       │             │         └──────────┬───────────┘   │
│       └─────────────┴────────────────────┘               │
└────────────────────────────┬────────────────────────────┘
                             │ HTTP/JSON
                             │
┌────────────────────────────┴────────────────────────────┐
│                  Spring Boot Backend                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐  │
│  │Controllers│→│ Services │→│Repositories│→│ MongoDB│  │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Key Features to Implement

### Core Functionality
- ✅ Board management (create, read, update, delete)
- ✅ Column management (create, read, update, delete)
- ✅ Task management (create, read, update, delete)
- ✅ Drag and drop with workflow rules
- ✅ Activity logging
- ✅ Task metadata (priority, assignee, due date, tags)

### Frontend Features
- ✅ Responsive design
- ✅ Dark mode support
- ✅ Real-time UI updates
- ✅ Optimistic updates
- ✅ Error handling
- ✅ Loading states

### Backend Features
- ✅ RESTful API design
- ✅ Data validation
- ✅ CORS support
- ✅ Error handling
- ✅ Activity logging

## File Structure

### Backend
```
backend/
├── src/main/kotlin/org/widmerkillenberger/backend/
│   ├── BackendApplication.kt
│   ├── config/
│   │   └── CorsConfig.kt
│   ├── controller/
│   │   ├── BoardController.kt
│   │   ├── ColumnController.kt
│   │   ├── TaskController.kt
│   │   └── ActivityLogController.kt
│   ├── service/
│   │   ├── BoardService.kt
│   │   ├── ColumnService.kt
│   │   ├── TaskService.kt
│   │   └── ActivityLogService.kt
│   ├── repository/
│   │   ├── BoardRepository.kt
│   │   ├── ColumnRepository.kt
│   │   ├── TaskRepository.kt
│   │   └── ActivityLogRepository.kt
│   ├── model/
│   │   ├── entity/
│   │   │   ├── Board.kt
│   │   │   ├── Column.kt
│   │   │   ├── Task.kt
│   │   │   ├── ActivityLog.kt
│   │   │   └── WorkflowRule.kt
│   │   └── dto/
│   │       ├── BoardDTO.kt
│   │       ├── ColumnDTO.kt
│   │       ├── TaskDTO.kt
│   │       └── ActivityLogDTO.kt
│   └── exception/
│       └── GlobalExceptionHandler.kt
└── src/main/resources/
    └── application.properties
```

### Frontend
```
frontend/
├── app/
│   ├── page.tsx
│   └── layout.tsx
├── components/
│   ├── kanban-board.tsx
│   ├── kanban-column.tsx
│   ├── task-card.tsx
│   └── ... (existing components)
├── lib/
│   ├── api/
│   │   ├── client.ts
│   │   ├── boards.ts
│   │   ├── columns.ts
│   │   ├── tasks.ts
│   │   └── activity-logs.ts
│   └── utils.ts
├── types/
│   └── api.ts
└── .env.local
```

## Environment Variables

### Frontend (.env.local)
```
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

### Backend (application.properties)
```
spring.application.name=backend
server.port=8080
spring.data.mongodb.uri=mongodb://localhost:27017/kanban
```

## Next Steps

1. **Start with Backend**: Begin by setting up the MongoDB connection and creating the data models
2. **Implement API Endpoints**: Create controllers and services for all CRUD operations
3. **Test Backend**: Use Postman or curl to test all API endpoints
4. **Create Frontend API Client**: Build the API service layer
5. **Integrate Frontend**: Replace mock data with API calls
6. **Test End-to-End**: Verify all functionality works together
7. **Polish & Document**: Add error handling, loading states, and documentation

## Notes

- The frontend already has a complete UI implementation with mock data
- The backend is a fresh Spring Boot project with no API endpoints yet
- MongoDB is the chosen database (already commented out in build.gradle.kts)
- Focus on core functionality first, optional enhancements can be added later
- All data models are already defined in the frontend TypeScript types
