# Kanban MCP Proxy Server

A lightweight MCP server that proxies requests to your Kanban REST API backend.

## Architecture

```
┌─────────────────┐
│   MCP Client    │ (AI/LLM)
│  (Kilo Code)    │
└────────┬────────┘
         │ stdio (MCP Protocol)
         │
┌────────▼────────┐
│   MCP Proxy     │ (Node.js - this server)
│   (No Database) │
└────────┬────────┘
         │ HTTP REST API
         │
┌────────▼────────┐
│  Spring Boot    │ (Your backend)
│  + MongoDB      │
└─────────────────┘
         │
         │
┌────────▼────────┐
│   Frontend      │ (Next.js)
│   Website       │
└─────────────────┘
```

## Setup

1. **Install dependencies:**
   ```bash
   cd mcp-proxy
   npm install
   ```

2. **Start your backend:**
   ```bash
   cd backend
   ./gradlew bootRun
   ```
   Backend will run on http://localhost:8080

3. **Update MCP settings:**
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

4. **Reload MCP client** (Kilo Code)

## Available Tools

The MCP proxy provides these tools to AI:

- `list_boards` - Get all Kanban boards
- `get_board` - Get a specific board with columns and tasks
- `create_task` - Create a new task in a column
- `update_task` - Update a task
- `move_task` - Move a task to a different column
- `delete_task` - Delete a task

## Testing

Test the MCP server manually:
```bash
echo '{"jsonrpc":"2.0","id":"1","method":"tools/list","params":{}}' | node index.js
```

## Benefits of This Architecture

✅ **Clean separation** - MCP server is separate from backend
✅ **No database access** - MCP proxy only calls REST API
✅ **Both can run simultaneously** - Backend serves frontend AND MCP
✅ **Lightweight** - Small Node.js proxy vs heavy Spring Boot
✅ **Easy to maintain** - Simple HTTP client, no business logic
✅ **Standard MCP** - Pure MCP implementation, no mixing concerns

## Comparison to Previous Approach

### ❌ Old (Embedded MCP in Backend)
- Backend had to run in "MCP mode" OR "web mode"
- Couldn't use website while MCP was active
- Mixed concerns (REST API + MCP + Database)
- Complex Spring Boot startup

### ✅ New (Separate MCP Proxy)
- Backend runs normally (REST API + website)
- MCP proxy is independent
- Clear separation of concerns
- Quick startup, easy debugging
