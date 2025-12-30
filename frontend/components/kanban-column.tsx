"use client"

import { useDroppable } from "@dnd-kit/core"
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable"
import type { Board, Column, Task, ActivityLog } from "@/app/page"
import { SortableTaskCard } from "./sortable-task-card"
import { MoreHorizontal, Plus, Pencil, Trash2 } from "lucide-react"
import { Button } from "./ui/button"
import { Badge } from "./ui/badge"
import { useState } from "react"
import { AddTaskDialog } from "./add-task-dialog"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "./ui/dropdown-menu"
import { EditColumnDialog } from "./edit-column-dialog"

type KanbanColumnProps = {
  column: Column
  board: Board
  onBoardChange: (board: Board) => void
  onUpdateColumn: (columnId: string, newTitle: string) => void
  onDeleteColumn: (columnId: string) => void
}

export function KanbanColumn({ column, board, onBoardChange, onUpdateColumn, onDeleteColumn }: KanbanColumnProps) {
  const { setNodeRef } = useDroppable({
    id: column.id,
  })

  const [showAddTaskDialog, setShowAddTaskDialog] = useState(false)
  const [showEditColumnDialog, setShowEditColumnDialog] = useState(false)

  const handleAddTask = (task: Omit<Task, "id" | "activityLog">) => {
    const newTask: Task = {
      ...task,
      id: Date.now().toString(),
      activityLog: [
        {
          id: Date.now().toString(),
          taskId: Date.now().toString(),
          boardId: board.id,
          action: "created",
          description: `Task created in "${column.title}"`,
          timestamp: new Date().toISOString(),
          user: "Current User",
        },
      ],
    }

    const newColumns = board.columns.map((col) =>
      col.id === column.id ? { ...col, tasks: [...col.tasks, newTask] } : col,
    )
    onBoardChange({ ...board, columns: newColumns })
  }

  const handleUpdateTask = (taskId: string, updatedTask: Partial<Task>) => {
    const task = column.tasks.find((t) => t.id === taskId)
    if (!task) return

    const logEntry: ActivityLog = {
      id: Date.now().toString(),
      taskId,
      boardId: board.id,
      action: "updated",
      description: `Task updated`,
      timestamp: new Date().toISOString(),
      user: "Current User",
    }

    const newTask = {
      ...task,
      ...updatedTask,
      activityLog: [...(task.activityLog || []), logEntry],
    }

    const newColumns = board.columns.map((col) => ({
      ...col,
      tasks: col.tasks.map((t) => (t.id === taskId ? newTask : t)),
    }))

    onBoardChange({ ...board, columns: newColumns })
  }

  const handleDeleteTask = (taskId: string) => {
    const newColumns = board.columns.map((col) => ({
      ...col,
      tasks: col.tasks.filter((t) => t.id !== taskId),
    }))
    onBoardChange({ ...board, columns: newColumns })
  }

  return (
    <>
      <div className="flex flex-col w-[320px] flex-shrink-0 bg-muted/30 rounded-xl p-4 h-full">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <h3 className="font-semibold text-sm">{column.title}</h3>
            <Badge variant="secondary" className="h-5 px-2 text-xs">
              {column.tasks.length}
            </Badge>
          </div>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" className="h-8 w-8">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setShowEditColumnDialog(true)}>
                <Pencil className="h-4 w-4 mr-2" />
                Edit Column
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => onDeleteColumn(column.id)} className="text-destructive">
                <Trash2 className="h-4 w-4 mr-2" />
                Delete Column
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <div ref={setNodeRef} className="flex-1 overflow-y-auto space-y-3 min-h-[100px]">
          <SortableContext items={column.tasks.map((t) => t.id)} strategy={verticalListSortingStrategy}>
            {column.tasks.map((task) => (
              <SortableTaskCard key={task.id} task={task} onUpdate={handleUpdateTask} onDelete={handleDeleteTask} />
            ))}
          </SortableContext>
        </div>

        <Button
          variant="ghost"
          onClick={() => setShowAddTaskDialog(true)}
          className="w-full mt-3 justify-start text-muted-foreground hover:text-foreground"
          size="sm"
        >
          <Plus className="h-4 w-4 mr-2" />
          Add Task
        </Button>
      </div>

      <AddTaskDialog open={showAddTaskDialog} onOpenChange={setShowAddTaskDialog} onAdd={handleAddTask} />
      <EditColumnDialog
        open={showEditColumnDialog}
        onOpenChange={setShowEditColumnDialog}
        column={column}
        onUpdate={onUpdateColumn}
      />
    </>
  )
}
