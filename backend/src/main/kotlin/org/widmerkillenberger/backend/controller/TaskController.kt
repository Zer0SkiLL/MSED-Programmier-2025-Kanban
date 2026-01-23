package org.widmerkillenberger.backend.controller

import jakarta.validation.Valid
import org.widmerkillenberger.backend.model.dto.*
import org.widmerkillenberger.backend.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/boards/{boardId}/columns/{columnId}/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    fun getTasksByColumnId(
        @PathVariable columnId: String
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksByColumnId(columnId)
        val responses = tasks.map { task ->
            TaskResponse(
                id = task.id ?: "",
                columnId = task.columnId,
                title = task.title,
                description = task.description,
                assignee = task.assignee,
                priority = task.priority ?: "medium",
                tags = task.tags ?: emptyList(),
                dueDate = task.dueDate,
                createdAt = task.createdAt?.toString() ?: "",
                updatedAt = task.updatedAt?.toString() ?: "",
                activityLog = task.activityLog?.map { log ->
                    ActivityLogDTO(
                        id = log.id,
                        boardId = log.boardId ?: "",
                        taskId = log.taskId,
                        action = log.action,
                        description = log.description,
                        timestamp = log.timestamp
                    )
                } ?: emptyList()
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/{taskId}")
    fun getTaskById(
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponse> {
        val task = taskService.getTaskById(taskId)
        return if (task != null) {
            val response = TaskResponse(
                id = task.id ?: "",
                columnId = task.columnId,
                title = task.title,
                description = task.description,
                assignee = task.assignee,
                priority = task.priority ?: "medium",
                tags = task.tags ?: emptyList(),
                dueDate = task.dueDate,
                createdAt = task.createdAt?.toString() ?: "",
                updatedAt = task.updatedAt?.toString() ?: "",
                activityLog = task.activityLog?.map { log ->
                    ActivityLogDTO(
                        id = log.id,
                        boardId = log.boardId ?: "",
                        taskId = log.taskId,
                        action = log.action,
                        description = log.description,
                        timestamp = log.timestamp
                    )
                } ?: emptyList()
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createTask(
        @PathVariable boardId: String,
        @PathVariable columnId: String,
        @Valid @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskResponse> {
        val task = taskService.createTask(
            boardId = boardId,
            columnId = columnId,
            title = request.title,
            description = request.description,
            assignee = request.assignee,
            priority = request.priority,
            dueDate = request.dueDate?.toLongOrNull(),
            tags = request.tags,
            position = null
        )
        val response = TaskResponse(
            id = task.id ?: "",
            columnId = task.columnId,
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            priority = task.priority ?: "medium",
            tags = task.tags ?: emptyList(),
            dueDate = task.dueDate,
            createdAt = task.createdAt?.toString() ?: "",
            updatedAt = task.updatedAt?.toString() ?: "",
            activityLog = task.activityLog?.map { log ->
                ActivityLogDTO(
                    id = log.id,
                    boardId = log.boardId ?: "",
                    taskId = log.taskId,
                    action = log.action,
                    description = log.description,
                    timestamp = log.timestamp
                )
            } ?: emptyList()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{taskId}")
    fun updateTask(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskRequest
    ): ResponseEntity<TaskResponse> {
        val task = taskService.updateTask(
            taskId = taskId,
            title = request.title,
            description = request.description,
            assignee = request.assignee,
            priority = request.priority,
            status = null,
            dueDate = request.dueDate?.toLongOrNull(),
            tags = request.tags
        )
        return if (task != null) {
            val response = TaskResponse(
                id = task.id ?: "",
                columnId = task.columnId,
                title = task.title,
                description = task.description,
                assignee = task.assignee,
                priority = task.priority ?: "medium",
                tags = task.tags ?: emptyList(),
                dueDate = task.dueDate,
                createdAt = task.createdAt?.toString() ?: "",
                updatedAt = task.updatedAt?.toString() ?: "",
                activityLog = task.activityLog?.map { log ->
                    ActivityLogDTO(
                        id = log.id,
                        boardId = log.boardId ?: "",
                        taskId = log.taskId,
                        action = log.action,
                        description = log.description,
                        timestamp = log.timestamp
                    )
                } ?: emptyList()
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable taskId: String
    ): ResponseEntity<Void> {
        val deleted = taskService.deleteTask(taskId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{taskId}/move")
    fun moveTask(
        @PathVariable taskId: String,
        @Valid @RequestBody request: MoveTaskRequest
    ): ResponseEntity<TaskResponse> {
        val task = taskService.moveTask(
            taskId = taskId,
            targetColumnId = request.targetColumnId,
            newPosition = request.position
        )
        return if (task != null) {
            val response = TaskResponse(
                id = task.id ?: "",
                columnId = task.columnId,
                title = task.title,
                description = task.description,
                assignee = task.assignee,
                priority = task.priority ?: "medium",
                tags = task.tags ?: emptyList(),
                dueDate = task.dueDate,
                createdAt = task.createdAt?.toString() ?: "",
                updatedAt = task.updatedAt?.toString() ?: ""
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
