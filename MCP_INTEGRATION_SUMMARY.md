# Kanban MCP Integration - Final Architecture

## Overview

This project integrates AI capabilities with the Kanban board through the Model Context Protocol (MCP) using a lightweight proxy server architecture.

## Architecture

```
┌──────────────────┐
│   AI Client      │ (Kilo Code / Claude Desktop)
│   MCP Consumer   │
└────────┬─────────┘
         │ stdio (JSON-RPC 2.0)
         │
┌────────▼─────────┐
│   MCP Proxy      │ Node.js Server
│   (Node.js)      │ /mcp-proxy/index.js
│   No Database    │
└────────┬─────────┘
         │ HTTP REST API
         │
┌────────▼─────────┐
│  Spring Boot     │ Kotlin Backend
│  REST API        │ /backend/
│  + MongoDB       │
└────────┬─────────┘
         │
         ├─────────────┬──────────────┐
         │             │              │
┌────────▼──────┐ ┌────▼─────┐ ┌─────▼──────┐
│   Frontend    │ │ MongoDB  │ │ MCP Proxy  │
│   (Next.js)   │ │ Database │ │            │
└───────────────┘ └──────────┘ └────────────┘
```

## Components

### 1. Backend (Spring Boot + Kotlin)
- **Location**: `/backend/`
- **Purpose**: Core REST API and business logic
- **Endpoints**: 
  - `/api/boards` - Board management
  - `/api/boards/{boardId}/columns` - Column management
  - `/api/boards/{boardId}/columns/{columnId}/tasks` - Task management
- **Database**: MongoDB on localhost:27017
- **Port**: 8080

### 2. MCP Proxy (Node.js)
- **Location**: `/mcp-proxy/`
- **Purpose**: Lightweight HTTP-to-stdio bridge for MCP protocol
- **Key Features**:
  - Communicates with AI via stdio (JSON-RPC 2.0)
  - Calls backend REST API via HTTP
  - No database access
  - No business logic
  - Stateless

### 3. Frontend (Next.js)
- **Location**: `/frontend/`
- **Purpose**: Web UI for human users
- **Port**: 3000
- **Connects to**: Backend REST API

## MCP Tools Available

The MCP proxy exposes these tools to AI:

| Tool | Description | Parameters |
|------|-------------|------------|
| `list_boards` | Get all Kanban boards | - |
| `get_board` | Get board with columns/tasks | `boardId` |
| `create_task` | Create a new task | `columnId`, `title`, `description`, `priority` |
| `update_task` | Update task properties | `taskId`, `title`, `description`, `priority` |
| `move_task` | Move task between columns | `taskId`, `columnId`, `position` |
| `delete_task` | Delete a task | `taskId` |

## Running the System

### 1. Start MongoDB
```bash
mongod --dbpath /data/db
```

### 2. Start Backend
```bash
cd backend
./gradlew bootRun
```
Backend runs on http://localhost:8080

### 3. Start Frontend (Optional)
```bash
cd frontend
npm run dev
```
Frontend runs on http://localhost:3000

### 4. Configure MCP Client
Edit your `mcp_settings.json`:
```json
{
  "mcpServers": {
    "kanban": {
      "command": "node",
      "args": [
        "/absolute/path/to/modern-kanban/mcp-proxy/index.js"
      ],
      "type": "stdio",
      "env": {
        "BACKEND_URL": "http://localhost:8080/api"
      }
    }
  }
}
```

### 5. Connect MCP Client
The MCP client (Kilo Code) will automatically start the proxy server.

## Key Design Decisions

### Why Separate MCP Proxy?

**Rejected Approach**: Embedded MCP server in Spring Boot
- ❌ Mixing concerns (REST API + MCP + Database)
- ❌ Complex Spring Boot profile management
- ❌ Can't run website when MCP is active
- ❌ Heavy Spring Boot process for simple protocol translation

**Final Approach**: Lightweight Node.js proxy
- ✅ Clean separation of concerns
- ✅ Backend always runs normally
- ✅ Website and AI work simultaneously
- ✅ Quick startup, easy debugging
- ✅ HTTP client only, no business logic

### Protocol Flow

1. **AI → MCP Proxy**: JSON-RPC 2.0 over stdio
   ```json
   {"jsonrpc":"2.0","id":"1","method":"tools/call","params":{"tool":"create_task",...}}
   ```

2. **MCP Proxy → Backend**: HTTP REST
   ```bash
   POST /api/boards/{boardId}/columns/{columnId}/tasks
   Content-Type: application/json
   {"title":"...","description":"...","priority":"HIGH"}
   ```

3. **Backend → MCP Proxy**: JSON response
   ```json
   {"id":"...","title":"...","priority":"HIGH",...}
   ```

4. **MCP Proxy → AI**: JSON-RPC 2.0 response
   ```json
   {"jsonrpc":"2.0","id":"1","result":{"content":[{"type":"text","text":"..."}]}}
   ```

## Files Structure

```
modern-kanban/
├── backend/                      # Spring Boot Kotlin backend
│   ├── src/main/kotlin/         # Source code
│   ├── src/main/resources/
│   │   └── application.properties
│   └── build.gradle.kts
│
├── mcp-proxy/                    # MCP proxy server
│   ├── index.js                 # Main MCP server
│   ├── package.json
│   └── README.md
│
├── frontend/                     # Next.js frontend
│   ├── app/
│   ├── components/
│   └── package.json
│
└── MCP_INTEGRATION_SUMMARY.md   # This file
```

## Testing

### Manual Test (curl)
```bash
# Create a task manually
curl -X POST http://localhost:8080/api/boards/696121820cf02ed843adad13/columns/696122080cf02ed843adad1a/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","description":"Test","priority":"HIGH"}'
```

### Via MCP Client
Use the AI interface to ask:
- "List all boards"
- "Create a task called 'Test AI Task' in the Inbox column with HIGH priority"
- "Move task X to the Done column"

## Success Criteria

✅ Backend runs normally with REST API
✅ Frontend can access backend
✅ MCP proxy connects to AI client
✅ AI can create, read, update, and delete tasks
✅ All systems work simultaneously
✅ Clean, separable architecture

## Troubleshooting

### MCP client won't connect
1. Check backend is running: `curl http://localhost:8080/api/boards`
2. Check MCP proxy path in `mcp_settings.json`
3. Restart MCP client

### Task creation fails
1. Verify backend is accessible
2. Check backend logs for errors
3. Ensure MongoDB is running
4. Test with curl first

### Can't find columnId/boardId
1. Use `list_boards` tool to get board IDs
2. Use `get_board` to see columns and their IDs
3. Columns are nested under boards in the API

## Future Enhancements

- Add resource templates for dynamic board/column discovery
- Implement webhooks for real-time AI notifications
- Add caching layer in MCP proxy
- Support batch operations
- Add authentication/authorization
