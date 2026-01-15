"use client"

import { useRef, useState } from "react"
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
import type { Board, Column, Task, ActivityLog } from "@/lib/api"
import { KanbanColumn } from "./kanban-column"
import { TaskCard } from "./task-card"
import { Plus } from "lucide-react"
import { Button } from "./ui/button"
import { AddColumnDialog } from "./add-column-dialog"
import { columnsApi, tasksApi } from "@/lib/api"

type KanbanBoardProps = {
  board: Board
  onBoardChange: (board: Board) => void
}

export function KanbanBoard({ board, onBoardChange }: KanbanBoardProps) {
  const [activeTask, setActiveTask] = useState<Task | null>(null)
  const [showAddColumnDialog, setShowAddColumnDialog] = useState(false)
  const [hoveredColumnId, setHoveredColumnId] = useState<string | null>(null)
  const lastOverColumnId = useRef<string | null>(null)
  const originalColumnId = useRef<string | null>(null)

  const sensors = useSensors(
      useSensor(PointerSensor, {
        activationConstraint: {
          distance: 8,
        },
      }),
  )

  const handleDragStart = (event: DragStartEvent) => {
    console.log("Drag Start Event:", event)
    const { active } = event
    const task = findTask(active.id as string)
    const column = findColumn(active.id as string)
    originalColumnId.current = column?.id || null
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
    const { over } = event
    if (!over || !activeTask) return

    const overColumn = findColumnByTaskOrColumnId(over.id as string)
    if (!overColumn) return

    // Avoid re-running for the same column
    if (lastOverColumnId.current === overColumn.id) return
    lastOverColumnId.current = overColumn.id

    setHoveredColumnId(overColumn.id)

    const newColumns = board.columns.map((col) => {
      // 1️⃣ Remove activeTask from every column
      const filteredTasks = col.tasks.filter(t => t.id !== activeTask.id)

      // 2️⃣ Insert into hovered column
      if (col.id === overColumn.id) {
        return {
          ...col,
          tasks: [...filteredTasks, activeTask],
        }
      }

      return {
        ...col,
        tasks: filteredTasks,
      }
    })

    onBoardChange({ ...board, columns: newColumns })
  }

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active } = event
    const activeId = active.id as string

    const fromColumnId = originalColumnId.current
    const toColumnId = lastOverColumnId.current

    if (!fromColumnId || !toColumnId) return
    if (fromColumnId === toColumnId) return
    if (!canMoveTask(fromColumnId, toColumnId)) return

    const task = findTask(activeId)
    if (!task) return

    try {
      await tasksApi.move(
          board.id,
          fromColumnId,
          task.id,
          { targetColumnId: toColumnId, position: 0 }
      )
    } catch (e) {
      console.error(e)
      // ❗ rollback if needed - revert to original state
      const newColumns = board.columns.map((col) => {
        if (col.id === toColumnId) {
          return {
            ...col,
            tasks: col.tasks.filter(t => t.id !== activeId),
          }
        }
        if (col.id === fromColumnId) {
          return {
            ...col,
            tasks: [...col.tasks, task],
          }
        }
        return col
      })
      onBoardChange({ ...board, columns: newColumns })
    }

    setActiveTask(null)
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

  const handleAddColumn = async (title: string) => {
    try {
      const newColumn = await columnsApi.create(board.id, { title })
      onBoardChange({ ...board, columns: [...board.columns, newColumn] })
    } catch (error) {
      console.error("Error creating column:", error)
    }
  }

  const handleUpdateColumn = async (columnId: string, newTitle: string) => {
    try {
      const updatedColumn = await columnsApi.update(board.id, columnId, { title: newTitle })
      const newColumns = board.columns.map((col) => (col.id === columnId ? updatedColumn : col))
      onBoardChange({ ...board, columns: newColumns })
    } catch (error) {
      console.error("Error updating column:", error)
    }
  }

  const handleDeleteColumn = async (columnId: string) => {
    try {
      await columnsApi.delete(board.id, columnId)
      const newColumns = board.columns.filter((col) => col.id !== columnId)
      onBoardChange({ ...board, columns: newColumns })
    } catch (error) {
      console.error("Error deleting column:", error)
    }
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