const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export interface ActivityLog {
    id: string
    taskId?: string
    boardId?: string
    action: string
    description: string
    timestamp: string
}

export interface Task {
    id: string
    title: string
    description?: string
    priority?: 'low' | 'medium' | 'high'
    assignee?: string
    dueDate?: string
    tags?: string[]
    activityLog?: ActivityLog[]
}

export interface Column {
    id: string
    name: string
    tasks: Task[]
}

export interface WorkflowRule {
    fromColumnId: string
    toColumnIds: string[]
}

export interface Board {
    id: string
    name: string
    columns: Column[]
    workflowRules?: WorkflowRule[]
    activityLog?: ActivityLog[]
}

export interface CreateBoardRequest {
    name: string
}

export interface UpdateBoardRequest {
    name: string
}

export interface CreateColumnRequest {
    title: string
}

export interface UpdateColumnRequest {
    title: string
}

export interface CreateTaskRequest {
    title: string
    description?: string
    priority?: 'low' | 'medium' | 'high'
    assignee?: string
    dueDate?: string
    tags?: string[]
}

export interface UpdateTaskRequest {
    title?: string
    description?: string
    priority?: 'low' | 'medium' | 'high'
    assignee?: string
    dueDate?: string
    tags?: string[]
}

export interface MoveTaskRequest {
    targetColumnId: string
    position: number
}

class ApiError extends Error {
    constructor(
        message: string,
        public status?: number,
        public response?: any
    ) {
        super(message)
        this.name = 'ApiError'
    }
}

async function handleResponse(response: Response): Promise<any> {
    if (!response.ok) {
        let errorMessage = 'An error occurred'
        try {
            const errorData = await response.json()
            errorMessage = errorData.message || errorData.error || errorMessage
        } catch (e) {
            errorMessage = response.statusText || errorMessage
        }
        throw new ApiError(errorMessage, response.status)
    }

    // Handle 204 No Content
    if (response.status === 204) {
        return null
    }

    return response.json()
}

// Boards API
export const boardsApi = {
    getAll: async (): Promise<Board[]> => {
        const response = await fetch(`${API_BASE_URL}/api/boards`)
        return handleResponse(response)
    },

    getById: async (id: string): Promise<Board> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${id}`)
        return handleResponse(response)
    },

    create: async (data: CreateBoardRequest): Promise<Board> => {
        const response = await fetch(`${API_BASE_URL}/api/boards`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    update: async (id: string, data: UpdateBoardRequest): Promise<Board> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    delete: async (id: string): Promise<void> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${id}`, {
            method: 'DELETE',
        })
        return handleResponse(response)
    },
}

// Columns API
export const columnsApi = {
    getAll: async (boardId: string): Promise<Column[]> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns`)
        return handleResponse(response)
    },

    create: async (boardId: string, data: CreateColumnRequest): Promise<Column> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    update: async (boardId: string, columnId: string, data: UpdateColumnRequest): Promise<Column> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns/${columnId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    delete: async (boardId: string, columnId: string): Promise<void> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns/${columnId}`, {
            method: 'DELETE',
        })
        return handleResponse(response)
    },
}

// Tasks API
export const tasksApi = {
    getAll: async (boardId: string, columnId: string): Promise<Task[]> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns/${columnId}/tasks`)
        return handleResponse(response)
    },

    create: async (boardId: string, columnId: string, data: CreateTaskRequest): Promise<Task> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/columns/${columnId}/tasks`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    update: async (taskId: string, data: UpdateTaskRequest): Promise<Task> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/dummy/columns/dummy/tasks/${taskId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },

    delete: async (taskId: string): Promise<void> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/dummy/columns/dummy/tasks/${taskId}`, {
            method: 'DELETE',
        })
        return handleResponse(response)
    },

    move: async (taskId: string, data: MoveTaskRequest): Promise<Task> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/dummy/columns/dummy/tasks/${taskId}/move`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },
}

// Activity Logs API
export const activityLogsApi = {
    getForBoard: async (boardId: string): Promise<ActivityLog[]> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/${boardId}/activity-logs`)
        return handleResponse(response)
    },

    getForTask: async (taskId: string): Promise<ActivityLog[]> => {
        const response = await fetch(`${API_BASE_URL}/api/boards/dummy/tasks/${taskId}/activity-logs`)
        return handleResponse(response)
    },
}

export { ApiError }