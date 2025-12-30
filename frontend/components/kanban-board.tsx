"use client"

import { useState } from "react"
import {
  DndContext,
  DragOverlay,
  closestCorners,
  type DragEndEvent,
  type DragStartEvent,
  type DragOverEvent,
  PointerSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core"
import type { Board, Column, Task, ActivityLog } from "@/app/page"
import { KanbanColumn } from "./kanban-column"
import { TaskCard } from "./task-card"
import { Plus } from "lucide-react"
import { Button } from "./ui/button"
import { AddColumnDialog } from "./add-column-dialog"
// import { useToast } from "@/hooks/use-toast"

type KanbanBoardProps = {
  board: Board
  onBoardChange: (board: Board) => void
}

export function KanbanBoard({ board, onBoardChange }: KanbanBoardProps) {
  const [activeTask, setActiveTask] = useState<Task | null>(null)
  const [showAddColumnDialog, setShowAddColumnDialog] = useState(false)
  // const { toast } = useToast()

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
  )

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event
    const task = findTask(active.id as string)
    setActiveTask(task || null)
  }

  const canMoveTask = (fromColumnId: string, toColumnId: string): boolean => {
    if (!board.workflowRules) return true
    if (fromColumnId === toColumnId) return true

    const rule = board.workflowRules.find((r) => r.fromColumnId === fromColumnId)
    if (!rule) return true

    return rule.toColumnIds.includes(toColumnId)
  }

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event
    if (!over) return

    const activeId = active.id as string
    const overId = over.id as string

    if (activeId === overId) return

    const activeColumn = findColumn(activeId)
    const overColumn = findColumnByTaskOrColumnId(overId)

    if (!activeColumn || !overColumn) return
    if (activeColumn.id === overColumn.id) return

    if (!canMoveTask(activeColumn.id, overColumn.id)) {
      return
    }

    const activeTask = activeColumn.tasks.find((t) => t.id === activeId)
    if (!activeTask) return

    const newColumns = board.columns.map((col) => {
      if (col.id === activeColumn.id) {
        return {
          ...col,
          tasks: col.tasks.filter((t) => t.id !== activeId),
        }
      }
      if (col.id === overColumn.id) {
        const overTask = col.tasks.find((t) => t.id === overId)
        if (overTask) {
          const overIndex = col.tasks.indexOf(overTask)
          const newTasks = [...col.tasks]
          newTasks.splice(overIndex, 0, activeTask)
          return { ...col, tasks: newTasks }
        }
        return { ...col, tasks: [...col.tasks, activeTask] }
      }
      return col
    })

    onBoardChange({ ...board, columns: newColumns })
  }

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event
    const activeId = active.id as string

    if (over) {
      const overId = over.id as string
      const activeColumn = findColumn(activeId)
      const overColumn = findColumnByTaskOrColumnId(overId)

      if (activeColumn && overColumn && activeColumn.id !== overColumn.id) {
        if (!canMoveTask(activeColumn.id, overColumn.id)) {
          const allowedColumns = board.workflowRules
            ?.find((r) => r.fromColumnId === activeColumn.id)
            ?.toColumnIds.map((id) => board.columns.find((c) => c.id === id)?.title)
            .join(", ")

          // toast({
          //   title: "Move not allowed",
          //   description: `Tasks from "${activeColumn.title}" can only be moved to: ${allowedColumns}`,
          //   variant: "destructive",
          // })

          setActiveTask(null)
          return
        }

        const task = findTask(activeId)
        if (task) {
          const logEntry: ActivityLog = {
            id: Date.now().toString(),
            taskId: task.id,
            boardId: board.id,
            action: "moved",
            description: `Moved from "${activeColumn.title}" to "${overColumn.title}"`,
            timestamp: new Date().toISOString(),
            user: "Current User",
          }

          const updatedTask = {
            ...task,
            activityLog: [...(task.activityLog || []), logEntry],
          }

          const newColumns = board.columns.map((col) => ({
            ...col,
            tasks: col.tasks.map((t) => (t.id === task.id ? updatedTask : t)),
          }))

          onBoardChange({ ...board, columns: newColumns })
        }
      }
    }

    setActiveTask(null)

    if (!over) return

    const overId = over.id as string

    if (activeId === overId) return

    const activeColumn = findColumn(activeId)
    const overColumn = findColumnByTaskOrColumnId(overId)

    if (!activeColumn || !overColumn) return

    if (activeColumn.id === overColumn.id) {
      const activeTask = activeColumn.tasks.find((t) => t.id === activeId)
      const overTask = activeColumn.tasks.find((t) => t.id === overId)

      if (!activeTask || !overTask) return

      const activeIndex = activeColumn.tasks.indexOf(activeTask)
      const overIndex = activeColumn.tasks.indexOf(overTask)

      if (activeIndex !== overIndex) {
        const newTasks = [...activeColumn.tasks]
        newTasks.splice(activeIndex, 1)
        newTasks.splice(overIndex, 0, activeTask)

        const newColumns = board.columns.map((col) => (col.id === activeColumn.id ? { ...col, tasks: newTasks } : col))
        onBoardChange({ ...board, columns: newColumns })
      }
    }
  }

  const findTask = (taskId: string): Task | undefined => {
    for (const column of board.columns) {
      const task = column.tasks.find((t) => t.id === taskId)
      if (task) return task
    }
  }

  const findColumn = (taskId: string): Column | undefined => {
    return board.columns.find((col) => col.tasks.some((t) => t.id === taskId))
  }

  const findColumnByTaskOrColumnId = (id: string): Column | undefined => {
    return board.columns.find((col) => col.id === id) || board.columns.find((col) => col.tasks.some((t) => t.id === id))
  }

  const handleAddColumn = (title: string) => {
    const newColumn: Column = {
      id: Date.now().toString(),
      title,
      tasks: [],
    }
    onBoardChange({ ...board, columns: [...board.columns, newColumn] })
  }

  const handleUpdateColumn = (columnId: string, newTitle: string) => {
    const newColumns = board.columns.map((col) => (col.id === columnId ? { ...col, title: newTitle } : col))
    onBoardChange({ ...board, columns: newColumns })
  }

  const handleDeleteColumn = (columnId: string) => {
    const newColumns = board.columns.filter((col) => col.id !== columnId)
    onBoardChange({ ...board, columns: newColumns })
  }

  return (
    <div className="h-full overflow-x-auto overflow-y-hidden px-6 py-6">
      <DndContext
        sensors={sensors}
        collisionDetection={closestCorners}
        onDragStart={handleDragStart}
        onDragOver={handleDragOver}
        onDragEnd={handleDragEnd}
      >
        <div className="flex gap-6 h-full pb-6">
          {board.columns.map((column) => (
            <KanbanColumn
              key={column.id}
              column={column}
              board={board}
              onBoardChange={onBoardChange}
              onUpdateColumn={handleUpdateColumn}
              onDeleteColumn={handleDeleteColumn}
            />
          ))}

          <div className="flex-shrink-0">
            <Button
              variant="outline"
              onClick={() => setShowAddColumnDialog(true)}
              className="h-full min-w-[280px] border-dashed hover:border-solid hover:bg-muted/50 bg-transparent"
            >
              <Plus className="h-5 w-5 mr-2" />
              Add Column
            </Button>
          </div>
        </div>

        <DragOverlay>{activeTask ? <TaskCard task={activeTask} isOverlay /> : null}</DragOverlay>
      </DndContext>

      <AddColumnDialog open={showAddColumnDialog} onOpenChange={setShowAddColumnDialog} onAdd={handleAddColumn} />
    </div>
  )
}
