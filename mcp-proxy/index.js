#!/usr/bin/env node

import { createInterface } from 'readline';
import axios from 'axios';

const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8080/api';

// MCP Protocol handler
class KanbanMCPServer {
  constructor() {
    this.serverInfo = {
      name: 'kanban-mcp-proxy',
      version: '1.0.0'
    };
  }

  async handleRequest(request) {
    try {
      const { method, params, id } = request;

      // Handle notifications (no response)
      if (method.startsWith('notifications/') || method === 'initialized') {
        console.error(`[MCP] Notification: ${method}`);
        return null;
      }

      console.error(`[MCP] Request: ${method}`);

      switch (method) {
        case 'initialize':
          return this.handleInitialize(id);
        
        case 'tools/list':
          return this.handleToolsList(id);
        
        case 'tools/call':
          return await this.handleToolsCall(id, params);
        
        case 'resources/list':
          return this.handleResourcesList(id);
        
        case 'resources/templates/list':
          return this.handleResourceTemplatesList(id);
        
        default:
          return {
            jsonrpc: '2.0',
            id,
            error: {
              code: -32601,
              message: `Method not found: ${method}`
            }
          };
      }
    } catch (error) {
      console.error('[MCP] Error:', error.message);
      return {
        jsonrpc: '2.0',
        id: request.id,
        error: {
          code: -32603,
          message: error.message
        }
      };
    }
  }

  handleInitialize(id) {
    return {
      jsonrpc: '2.0',
      id,
      result: {
        protocolVersion: '2024-11-05',
        capabilities: {
          tools: {},
          resources: {}
        },
        serverInfo: this.serverInfo
      }
    };
  }

  handleToolsList(id) {
    return {
      jsonrpc: '2.0',
      id,
      result: {
        tools: [
          {
            name: 'list_boards',
            description: 'Get all Kanban boards',
            inputSchema: {
              type: 'object',
              properties: {},
              required: []
            }
          },
          {
            name: 'get_board',
            description: 'Get a specific board with its columns and tasks',
            inputSchema: {
              type: 'object',
              properties: {
                boardId: {
                  type: 'string',
                  description: 'The ID of the board'
                }
              },
              required: ['boardId']
            }
          },
          {
            name: 'create_task',
            description: 'Create a new task in a column',
            inputSchema: {
              type: 'object',
              properties: {
                columnId: {
                  type: 'string',
                  description: 'The ID of the column'
                },
                title: {
                  type: 'string',
                  description: 'Task title'
                },
                description: {
                  type: 'string',
                  description: 'Task description (optional)'
                },
                priority: {
                  type: 'string',
                  description: 'Priority: LOW, MEDIUM, HIGH, or CRITICAL'
                }
              },
              required: ['columnId', 'title']
            }
          },
          {
            name: 'update_task',
            description: 'Update a task',
            inputSchema: {
              type: 'object',
              properties: {
                taskId: {
                  type: 'string',
                  description: 'The ID of the task'
                },
                title: {
                  type: 'string',
                  description: 'New task title (optional)'
                },
                description: {
                  type: 'string',  
                  description: 'New task description (optional)'
                },
                priority: {
                  type: 'string',
                  description: 'New priority (optional)'
                }
              },
              required: ['taskId']
            }
          },
          {
            name: 'move_task',
            description: 'Move a task to a different column',
            inputSchema: {
              type: 'object',
              properties: {
                taskId: {
                  type: 'string',
                  description: 'The ID of the task'
                },
                columnId: {
                  type: 'string',
                  description: 'The ID of the target column'
                },
                position: {
                  type: 'number',
                  description: 'Position in the column (optional)'
                }
              },
              required: ['taskId', 'columnId']
            }
          },
          {
            name: 'delete_task',
            description: 'Delete a task',
            inputSchema: {
              type: 'object',
              properties: {
                taskId: {
                  type: 'string',
                  description: 'The ID of the task to delete'
                }
              },
              required: ['taskId']
            }
          }
        ]
      }
    };
  }

  async handleToolsCall(id, params) {
    const { name, arguments: args } = params;

    try {
      let result;

      switch (name) {
        case 'list_boards':
          const boardsResponse = await axios.get(`${BACKEND_URL}/boards`);
          result = {
            success: true,
            data: boardsResponse.data,
            message: `Found ${boardsResponse.data.length} boards`
          };
          break;

        case 'get_board':
          const boardResponse = await axios.get(`${BACKEND_URL}/boards/${args.boardId}?includeColumns=true&includeTasks=true`);
          result = {
            success: true,
            data: boardResponse.data,
            message: `Retrieved board: ${boardResponse.data.name}`
          };
          break;

        case 'create_task':
          // Get all boards to find which board contains this column
          const allBoards = await axios.get(`${BACKEND_URL}/boards`);
          let boardIdForCreate = null;
          
          for (const board of allBoards.data) {
            const columnsResponse = await axios.get(`${BACKEND_URL}/boards/${board.id}/columns`);
            if (columnsResponse.data.some(col => col.id === args.columnId)) {
              boardIdForCreate = board.id;
              break;
            }
          }
          
          if (!boardIdForCreate) {
            throw new Error(`Column ${args.columnId} not found in any board`);
          }
          
          const createResponse = await axios.post(`${BACKEND_URL}/boards/${boardIdForCreate}/columns/${args.columnId}/tasks`, {
            title: args.title,
            description: args.description || '',
            priority: args.priority || 'MEDIUM',
            assignee: args.assignee || null,
            tags: args.tags || []
          });
          result = {
            success: true,
            data: createResponse.data,
            message: `Task created: ${createResponse.data.title}`
          };
          break;

        case 'update_task':
          // Get task to find its board and column
          const taskResponse = await axios.get(`${BACKEND_URL}/tasks/${args.taskId}`);
          const task = taskResponse.data;
          const updateBoardId = task.boardId;
          const updateColumnId = task.columnId;
          
          const updateResponse = await axios.put(
            `${BACKEND_URL}/boards/${updateBoardId}/columns/${updateColumnId}/tasks/${args.taskId}`,
            {
              title: args.title,
              description: args.description,
              priority: args.priority,
              assignee: args.assignee,
              tags: args.tags
            }
          );
          result = {
            success: true,
            data: updateResponse.data,
            message: `Task updated: ${updateResponse.data.title}`
          };
          break;

        case 'move_task':
          const { taskId, columnId, position } = args;
          // Get task to find its board and current column
          const moveTaskResponse = await axios.get(`${BACKEND_URL}/tasks/${taskId}`);
          const moveTask = moveTaskResponse.data;
          const moveBoardId = moveTask.boardId;
          const moveCurrentColumnId = moveTask.columnId;
          
          const moveResponse = await axios.patch(
            `${BACKEND_URL}/boards/${moveBoardId}/columns/${moveCurrentColumnId}/tasks/${taskId}/move`,
            {
              targetColumnId: columnId,
              position
            }
          );
          result = {
            success: true,
            data: moveResponse.data,
            message: `Task moved to column ${columnId}`
          };
          break;

        case 'delete_task':
          // Get task to find its board and column
          const deleteTaskResponse = await axios.get(`${BACKEND_URL}/tasks/${args.taskId}`);
          const deleteTask = deleteTaskResponse.data;
          const deleteBoardId = deleteTask.boardId;
          const deleteColumnId = deleteTask.columnId;
          
          await axios.delete(`${BACKEND_URL}/boards/${deleteBoardId}/columns/${deleteColumnId}/tasks/${args.taskId}`);
          result = {
            success: true,
            message: `Task deleted: ${args.taskId}`
          };
          break;

        default:
          throw new Error(`Unknown tool: ${name}`);
      }

      return {
        jsonrpc: '2.0',
        id,
        result: {
          content: [
            {
              type: 'text',
              text: JSON.stringify(result, null, 2)
            }
          ]
        }
      };
    } catch (error) {
      console.error(`[MCP] Tool call error:`, error.message);
      return {
        jsonrpc: '2.0',
        id,
        error: {
          code: -32603,
          message: error.response?.data?.message || error.message
        }
      };
    }
  }

  handleResourcesList(id) {
    return {
      jsonrpc: '2.0',
      id,
      result: {
        resources: []
      }
    };
  }

  handleResourceTemplatesList(id) {
    return {
      jsonrpc: '2.0',
      id,
      result: {
        resourceTemplates: []
      }
    };
  }
}

// Main
async function main() {
  const server = new KanbanMCPServer();
  const rl = createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false
  });

  console.error('[MCP] Kanban MCP Proxy Server starting...');
  console.error(`[MCP] Backend URL: ${BACKEND_URL}`);

  rl.on('line', async (line) => {
    if (!line.trim()) return;

    try {
      const request = JSON.parse(line);
      const response = await server.handleRequest(request);
      
      if (response !== null) {
        console.log(JSON.stringify(response));
      }
    } catch (error) {
      console.error('[MCP] Parse error:', error.message);
    }
  });

  rl.on('close', () => {
    console.error('[MCP] Server stopped');
    process.exit(0);
  });
}

main().catch(error => {
  console.error('[MCP] Fatal error:', error);
  process.exit(1);
});
