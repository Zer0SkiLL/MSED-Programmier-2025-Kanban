"use client"

import type { Board, Column, Task } from "@/lib/api"
import { Card } from "./ui/card"
import { Badge } from "./ui/badge"
import { Calendar, GripVertical, MoreVertical, Pencil, Trash2 } from "lucide-react"
import { Avatar, AvatarFallback } from "./ui/avatar"
import { Button } from "./ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "./ui/dropdown-menu"
import { useState } from "react"
import { EditTaskDialog } from "./edit-task-dialog"

type TaskCardProps = {
  task: Task
  isOverlay?: boolean
  dragHandleProps?: any
  onUpdate?: (taskId: string, task: Partial<Task>) => void
  onDelete?: (taskId: string) => void
}

const priorityColors = {
  low: "bg-blue-500/10 text-blue-700 dark:text-blue-400 border-blue-500/20",
  medium: "bg-yellow-500/10 text-yellow-700 dark:text-yellow-400 border-yellow-500/20",
  high: "bg-red-500/10 text-red-700 dark:text-red-400 border-red-500/20",
}

export function TaskCard({ task, isOverlay, dragHandleProps, onUpdate, onDelete }: TaskCardProps) {
  const [showEditDialog, setShowEditDialog] = useState(false)

  return (
      <>
        <Card
            className={`p-4 group hover:shadow-md transition-shadow ${
                isOverlay ? "rotate-3 shadow-xl cursor-grabbing" : ""
            }`}
        >
          <div className="space-y-3">
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-start gap-2 flex-1 min-w-0">
                {!isOverlay && dragHandleProps && (
                    <div {...dragHandleProps} className="cursor-grab active:cursor-grabbing mt-0.5 flex-shrink-0">
                      <GripVertical className="h-4 w-4 text-muted-foreground" />
                    </div>
                )}
                <div className="flex-1 min-w-0">
                  <h4 className="font-medium text-sm leading-snug mb-1">{task.title}</h4>
                  {task.description && (
                      <p className="text-xs text-muted-foreground line-clamp-2 leading-relaxed">{task.description}</p>
                  )}
                </div>
              </div>
              {!isOverlay && onUpdate && onDelete && (
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button
                          variant="ghost"
                          size="icon"
                          className="h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity flex-shrink-0"
                      >
                        <MoreVertical className="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem onClick={() => setShowEditDialog(true)}>
                        <Pencil className="h-4 w-4 mr-2" />
                        Edit Task
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => onDelete(task.id)} className="text-destructive">
                        <Trash2 className="h-4 w-4 mr-2" />
                        Delete Task
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
              )}
            </div>

            {task.tags && task.tags.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {task.tags.map((tag) => (
                      <Badge key={tag} variant="secondary" className="text-xs px-2 py-0 h-5">
                        {tag}
                      </Badge>
                  ))}
                </div>
            )}

            <div className="flex items-center justify-between pt-2 border-t border-border">
              <div className="flex items-center gap-3">
                {task.assignee && (
                    <div className="flex items-center gap-1.5">
                      <Avatar className="h-6 w-6">
                        <AvatarFallback className="text-xs bg-accent text-accent-foreground">
                          {task.assignee
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                        </AvatarFallback>
                      </Avatar>
                    </div>
                )}
                {task.dueDate && (
                    <div className="flex items-center gap-1 text-muted-foreground">
                      <Calendar className="h-3.5 w-3.5" />
                      <span className="text-xs">
                    {new Date(task.dueDate).toLocaleDateString("en-US", {
                      month: "short",
                      day: "numeric",
                    })}
                  </span>
                    </div>
                )}
              </div>

              {task.priority && (
                  <Badge variant="outline" className={`text-xs px-2 py-0 h-5 ${priorityColors[task.priority]}`}>
                    {task.priority}
                  </Badge>
              )}
            </div>
          </div>
        </Card>

        {onUpdate && (
            <EditTaskDialog open={showEditDialog} onOpenChange={setShowEditDialog} task={task} onUpdate={onUpdate} />
        )}
      </>
  )
}