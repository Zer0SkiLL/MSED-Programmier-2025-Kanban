import { useSortable } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import type { Task } from "@/app/page"
import { TaskCard } from "./task-card"

type SortableTaskCardProps = {
  task: Task
  onUpdate: (taskId: string, task: Partial<Task>) => void
  onDelete: (taskId: string) => void
}

export function SortableTaskCard({ task, onUpdate, onDelete }: SortableTaskCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: task.id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  }

  return (
    <div ref={setNodeRef} style={style}>
      <TaskCard task={task} dragHandleProps={{ ...attributes, ...listeners }} onUpdate={onUpdate} onDelete={onDelete} />
    </div>
  )
}
