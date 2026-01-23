"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ScrollArea } from "@/components/ui/scroll-area"
import type { Task, ActivityLog } from "@/lib/api"
import { Calendar, Clock, Loader2 } from "lucide-react"
import { activityLogsApi } from "@/lib/api"

type EditTaskDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  task: Task
  boardId: string
  onUpdate: (taskId: string, task: Partial<Task>) => void
}

export function EditTaskDialog({ open, onOpenChange, task, boardId, onUpdate }: EditTaskDialogProps) {
  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(task.description || "")
  const [priority, setPriority] = useState<"low" | "medium" | "high">(task.priority || "medium")
  const [assignee, setAssignee] = useState(task.assignee || "")
  const [dueDate, setDueDate] = useState(task.dueDate || "")
  const [tags, setTags] = useState(task.tags?.join(", ") || "")
  const [logs, setLogs] = useState<ActivityLog[]>(task.activityLog || [])
  const [isLoadingLogs, setIsLoadingLogs] = useState(false)
  const [activeTab, setActiveTab] = useState("details")

  useEffect(() => {
    setTitle(task.title)
    setDescription(task.description || "")
    setPriority(task.priority || "medium")
    setAssignee(task.assignee || "")
    setDueDate(task.dueDate || "")
    setTags(task.tags?.join(", ") || "")
    setLogs(task.activityLog || [])
  }, [task])

  useEffect(() => {
    if (open && activeTab === "activity") {
      fetchLogs()
    }
  }, [open, activeTab, task.id])

  const fetchLogs = async () => {
    try {
      setIsLoadingLogs(true)
      const fetchedLogs = await activityLogsApi.getForTask(task.id)
      setLogs(fetchedLogs)
    } catch (error) {
      console.error("Failed to fetch activity logs:", error)
    } finally {
      setIsLoadingLogs(false)
    }
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!title.trim()) return

    onUpdate(task.id, {
      title: title.trim(),
      description: description.trim() || undefined,
      priority,
      assignee: assignee.trim() || undefined,
      dueDate: dueDate || undefined,
      tags: tags
        .split(",")
        .map((t) => t.trim())
        .filter((t) => t.length > 0),
    })

    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh]">
        <DialogHeader>
          <DialogTitle>Edit Task</DialogTitle>
        </DialogHeader>
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="details">Details</TabsTrigger>
            <TabsTrigger value="activity">Activity Log</TabsTrigger>
          </TabsList>
          <TabsContent value="details">
            <form onSubmit={handleSubmit}>
              <div className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="edit-title">Title *</Label>
                  <Input
                    id="edit-title"
                    placeholder="Enter task title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="edit-description">Description</Label>
                  <Textarea
                    id="edit-description"
                    placeholder="Enter task description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    rows={3}
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="edit-priority">Priority</Label>
                    <Select value={priority} onValueChange={(value: "low" | "medium" | "high") => setPriority(value)}>
                      <SelectTrigger id="edit-priority">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="low">Low</SelectItem>
                        <SelectItem value="medium">Medium</SelectItem>
                        <SelectItem value="high">High</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="edit-dueDate">Due Date</Label>
                    <Input id="edit-dueDate" type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
                  </div>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="edit-assignee">Assignee</Label>
                  <Input
                    id="edit-assignee"
                    placeholder="Enter assignee name"
                    value={assignee}
                    onChange={(e) => setAssignee(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="edit-tags">Tags (comma separated)</Label>
                  <Input
                    id="edit-tags"
                    placeholder="design, frontend, urgent"
                    value={tags}
                    onChange={(e) => setTags(e.target.value)}
                  />
                </div>
              </div>
              <DialogFooter>
                <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                  Cancel
                </Button>
                <Button type="submit">Save Changes</Button>
              </DialogFooter>
            </form>
          </TabsContent>
          <TabsContent value="activity" className="mt-4">
            <ScrollArea className="h-[400px] pr-4">
              {isLoadingLogs ? (
                <div className="flex flex-col items-center justify-center h-[200px] text-center">
                  <Loader2 className="h-8 w-8 animate-spin text-primary mb-2" />
                  <p className="text-sm text-muted-foreground">Loading activity...</p>
                </div>
              ) : logs && logs.length > 0 ? (
                <div className="space-y-4">
                  {[...logs].sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()).map((log) => (
                    <div key={log.id} className="flex gap-3 pb-4 border-b border-border last:border-0">
                      <div className="flex-shrink-0 mt-1">
                        <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center">
                          <Clock className="h-4 w-4 text-primary" />
                        </div>
                      </div>
                      <div className="flex-1 space-y-1">
                        <p className="text-sm font-medium">{log.description}</p>
                        <div className="flex items-center gap-2 text-xs text-muted-foreground">
                          <div className="flex items-center gap-1">
                            <Calendar className="h-3 w-3" />
                            <span>
                              {new Date(log.timestamp).toLocaleDateString("en-US", {
                                month: "short",
                                day: "numeric",
                                year: "numeric",
                              })}{" "}
                              at{" "}
                              {new Date(log.timestamp).toLocaleTimeString("en-US", {
                                hour: "2-digit",
                                minute: "2-digit",
                              })}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="flex flex-col items-center justify-center h-[200px] text-center">
                  <Clock className="h-12 w-12 text-muted-foreground/50 mb-2" />
                  <p className="text-sm text-muted-foreground">No activity logged yet</p>
                </div>
              )}
            </ScrollArea>
          </TabsContent>
        </Tabs>
      </DialogContent>
    </Dialog>
  )
}
