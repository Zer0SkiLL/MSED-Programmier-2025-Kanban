"use client"

import { useState } from "react"
import { KanbanBoard } from "@/components/kanban-board"
import { Header } from "@/components/header"
import { BoardSelector } from "@/components/board-selector"

export type ActivityLog = {
  id: string
  taskId?: string
  boardId?: string
  action: string
  description: string
  timestamp: string
  user: string
}

export type Task = {
  id: string
  title: string
  description?: string
  priority?: "low" | "medium" | "high"
  assignee?: string
  dueDate?: string
  tags?: string[]
  activityLog?: ActivityLog[]
}

export type Column = {
  id: string
  title: string
  tasks: Task[]
}

export type WorkflowRule = {
  fromColumnId: string
  toColumnIds: string[]
}

export type Board = {
  id: string
  name: string
  columns: Column[]
  workflowRules?: WorkflowRule[]
  activityLog?: ActivityLog[]
}

// Mock data - will be replaced with API calls to Spring Boot backend
const initialBoards: Board[] = [
  {
    id: "1",
    name: "Project Alpha",
    workflowRules: [
      { fromColumnId: "todo", toColumnIds: ["in-progress"] },
      { fromColumnId: "in-progress", toColumnIds: ["review", "todo"] },
      { fromColumnId: "review", toColumnIds: ["done", "in-progress"] },
      { fromColumnId: "done", toColumnIds: ["in-progress"] },
    ],
    activityLog: [],
    columns: [
      {
        id: "todo",
        title: "To Do",
        tasks: [
          {
            id: "1",
            title: "Design new landing page",
            description: "Create wireframes and mockups for the new landing page",
            priority: "high",
            assignee: "John Doe",
            tags: ["design", "frontend"],
            activityLog: [],
          },
          {
            id: "2",
            title: "Update documentation",
            description: "Add API documentation for new endpoints",
            priority: "medium",
            tags: ["docs"],
            activityLog: [],
          },
        ],
      },
      {
        id: "in-progress",
        title: "In Progress",
        tasks: [
          {
            id: "3",
            title: "Implement authentication",
            description: "Add JWT authentication to the backend",
            priority: "high",
            assignee: "Jane Smith",
            dueDate: "2025-01-15",
            tags: ["backend", "security"],
            activityLog: [],
          },
        ],
      },
      {
        id: "review",
        title: "Review",
        tasks: [
          {
            id: "4",
            title: "Code review for PR #123",
            description: "Review the new feature implementation",
            priority: "medium",
            assignee: "Bob Johnson",
            tags: ["review"],
            activityLog: [],
          },
        ],
      },
      {
        id: "done",
        title: "Done",
        tasks: [
          {
            id: "5",
            title: "Setup CI/CD pipeline",
            description: "Configure GitHub Actions for automated testing",
            priority: "high",
            tags: ["devops"],
            activityLog: [],
          },
        ],
      },
    ],
  },
]

export default function Page() {
  const [boards, setBoards] = useState<Board[]>(initialBoards)
  const [currentBoardId, setCurrentBoardId] = useState<string>(boards[0].id)

  const currentBoard = boards.find((b) => b.id === currentBoardId) || boards[0]

  const handleBoardChange = (boardId: string) => {
    setCurrentBoardId(boardId)
  }

  const handleColumnsChange = (newColumns: Column[]) => {
    setBoards(boards.map((board) => (board.id === currentBoardId ? { ...board, columns: newColumns } : board)))
  }

  const handleBoardsChange = (newBoards: Board[]) => {
    setBoards(newBoards)
  }

  const handleAddBoard = (boardName: string) => {
    const newBoard: Board = {
      id: Date.now().toString(),
      name: boardName,
      columns: [
        { id: "todo", title: "To Do", tasks: [] },
        { id: "in-progress", title: "In Progress", tasks: [] },
        { id: "review", title: "Review", tasks: [] },
        { id: "done", title: "Done", tasks: [] },
      ],
      workflowRules: [
        { fromColumnId: "todo", toColumnIds: ["in-progress"] },
        { fromColumnId: "in-progress", toColumnIds: ["review", "todo"] },
        { fromColumnId: "review", toColumnIds: ["done", "in-progress"] },
        { fromColumnId: "done", toColumnIds: ["in-progress"] },
      ],
      activityLog: [],
    }
    setBoards([...boards, newBoard])
    setCurrentBoardId(newBoard.id)
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header>
        <BoardSelector
          boards={boards}
          currentBoardId={currentBoardId}
          onBoardChange={handleBoardChange}
          onAddBoard={handleAddBoard}
        />
      </Header>
      <main className="flex-1 overflow-hidden">
        <KanbanBoard
          board={currentBoard}
          onBoardChange={(updatedBoard) => {
            setBoards(boards.map((b) => (b.id === currentBoardId ? updatedBoard : b)))
          }}
        />
      </main>
    </div>
  )
}
