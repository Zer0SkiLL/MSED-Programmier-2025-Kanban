# Modern Kanban Board

A full-stack kanban board application with drag-and-drop functionality, built with Next.js (frontend) and Spring Boot (backend).

## 🚀 Features

- **Board Management**: Create, edit, and delete multiple kanban boards
- **Column Management**: Add, edit, and remove columns within boards
- **Task Management**: Create, edit, delete, and move tasks between columns
- **Drag & Drop**: Intuitive drag-and-drop interface with workflow rules
- **Activity Logging**: Track all changes and movements
- **Task Metadata**: Priority levels, assignees, due dates, and tags
- **Dark Mode**: Built-in theme toggle
- **Responsive Design**: Works on desktop and mobile devices

## 🛠️ Tech Stack

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

## 📋 Project Status

### ✅ Completed
- Frontend UI with all components
- Drag-and-drop functionality
- Workflow rules implementation
- Activity logging system
- Dark mode support
- Responsive design

### 🚧 In Progress
- Backend API development
- Frontend-backend integration

### 📝 Planned
- Authentication/authorization
- Real-time updates (WebSocket)
- Advanced search and filtering
- Data export/import
- Team collaboration features

## 📚 Documentation

- **[DEVELOPMENT_PLAN.md](./DEVELOPMENT_PLAN.md)** - Detailed development plan with all tasks
- **[API_ENDPOINTS.md](./API_ENDPOINTS.md)** - Complete API reference documentation
- **[QUICK_START.md](./QUICK_START.md)** - Quick start guide for setup and development

## 🏗️ Architecture

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

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Node.js 18 or higher
- MongoDB (running locally or accessible)

### Quick Setup

1. **Start MongoDB**
   ```bash
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   ```

2. **Start Backend**
   ```bash
   cd backend
   ./gradlew bootRun
   ```

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   echo "NEXT_PUBLIC_API_BASE_URL=http://localhost:8080" > .env.local
   npm run dev
   ```

4. **Open in Browser**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

For detailed setup instructions, see [QUICK_START.md](./QUICK_START.md).

## 📁 Project Structure

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
│   │   ├── page.tsx          # Main page with state management
│   │   └── layout.tsx        # Root layout
│   ├── components/            # React components
│   │   ├── kanban-board.tsx  # Main board component
│   │   ├── kanban-column.tsx # Column component
│   │   ├── task-card.tsx     # Task card component
│   │   └── ...               # Other UI components
│   ├── lib/
│   │   ├── api/              # API services (to be created)
│   │   └── utils.ts          # Utility functions
│   └── .env.local            # Environment variables
│
├── DEVELOPMENT_PLAN.md        # Detailed development plan
├── API_ENDPOINTS.md           # API documentation
├── QUICK_START.md            # Quick start guide
└── README.md                 # This file
```

## 🔧 Development

### Backend Development

The backend is a Spring Boot application with Kotlin. Key areas to implement:

1. **Data Models**: Entities for Board, Column, Task, ActivityLog, WorkflowRule
2. **Repositories**: MongoDB repositories for data access
3. **Services**: Business logic and workflow validation
4. **Controllers**: REST API endpoints

See [DEVELOPMENT_PLAN.md](./DEVELOPMENT_PLAN.md) for detailed tasks.

### Frontend Development

The frontend is a Next.js application with React and TypeScript. Key areas to implement:

1. **API Client**: HTTP client configuration and error handling
2. **API Services**: Functions to interact with backend endpoints
3. **State Management**: Replace mock data with API calls
4. **Loading States**: Add loading indicators and error handling

See [DEVELOPMENT_PLAN.md](./DEVELOPMENT_PLAN.md) for detailed tasks.

## 🧪 Testing

### Backend Testing
```bash
cd backend
./gradlew test
```

### Frontend Testing
```bash
cd frontend
npm run lint
```

### API Testing
Use Postman, curl, or any HTTP client to test endpoints. See [API_ENDPOINTS.md](./API_ENDPOINTS.md) for endpoint details.

## 📊 Data Models

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

## 🔐 Security Considerations

- Input validation on both frontend and backend
- CORS configuration to prevent unauthorized access
- Error handling to prevent information leakage
- (Future) Authentication and authorization

## 🚢 Deployment

### Backend Deployment
1. Build the JAR file: `./gradlew build`
2. Deploy to a server or cloud platform
3. Configure MongoDB connection
4. Set environment variables

### Frontend Deployment
1. Build the application: `npm run build`
2. Deploy to Vercel, Netlify, or any hosting platform
3. Set `NEXT_PUBLIC_API_BASE_URL` environment variable

## 🤝 Contributing

This is a project for educational purposes. Feel free to fork and modify for your own use.

## 📄 License

This project is for educational purposes.

## 🙏 Acknowledgments

- UI components from [shadcn/ui](https://ui.shadcn.com/)
- Drag and drop from [@dnd-kit](https://dndkit.com/)
- Icons from [Lucide](https://lucide.dev/)
- Spring Boot framework
- Next.js framework

## 📞 Support

For issues or questions:
1. Check the documentation files
2. Review error messages in the terminal
3. Verify all services are running
4. Consult the [QUICK_START.md](./QUICK_START.md) for common issues

## 🗺️ Roadmap

### Phase 1: Core Functionality (Current)
- [x] Frontend UI
- [ ] Backend API
- [ ] Frontend-backend integration

### Phase 2: Enhancement
- [ ] Authentication
- [ ] Real-time updates
- [ ] Advanced filtering
- [ ] Data export/import

### Phase 3: Polish
- [ ] Performance optimization
- [ ] Comprehensive testing
- [ ] Documentation
- [ ] Deployment

---

**Note**: This project is currently in development. The frontend is complete with mock data, and the backend API is being implemented.
