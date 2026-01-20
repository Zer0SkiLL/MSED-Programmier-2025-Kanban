package org.widmerkillenberger.backend.service

import org.widmerkillenberger.backend.model.entity.Task
import org.widmerkillenberger.backend.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val columnService: ColumnService,
    private val boardService: BoardService,
    private val activityLogService: ActivityLogService
) {

    fun getTaskById(taskId: String): Task? {
        return taskRepository.findById(taskId).orElse(null)
    }

    fun getTasksByColumnId(columnId: String): List<Task> {
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId)
    }

    fun getTasksByBoardId(boardId: String): List<Task> {
        return taskRepository.findByBoardIdOrderByCreatedAtDesc(boardId)
    }

    fun createTask(
        boardId: String,
        columnId: String,
        title: String,
        description: String?,
        assignee: String?,
        priority: String?,
        dueDate: Long?,
        tags: List<String>?,
        position: Int?
    ): Task {
        val column = columnService.getColumnById(columnId)
            ?: throw IllegalArgumentException("Column not found with id: $columnId")
        val board = boardService.getBoardById(boardId)
            ?: throw IllegalArgumentException("Board not found with id: $boardId")
        
        val task = Task(
            id = null,
            boardId = boardId,
            columnId = columnId,
            title = title,
            description = description,
            assignee = assignee,
            priority = priority ?: "medium",
            status = "todo",
            dueDate = dueDate?.toString(),
            tags = tags ?: emptyList(),
            position = position ?: (taskRepository.countByColumnId(columnId).toInt()),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val savedTask = taskRepository.save(task)
        
        // Log activity
        activityLogService.logActivity(
            boardId = boardId,
            action = "TASK_CREATED",
            description = "Task '${title}' was created in column '${column.title}'",
            taskId = savedTask.id,
            columnId = columnId
        )
        
        return savedTask
    }

    fun updateTask(
        taskId: String,
        title: String?,
        description: String?,
        assignee: String?,
        priority: String?,
        status: String?,
        dueDate: Long?,
        tags: List<String>?
    ): Task? {
        val task = taskRepository.findById(taskId).orElse(null) ?: return null
        val column = columnService.getColumnById(task.columnId) ?: return null
        val board = boardService.getBoardById(task.boardId) ?: return null
        
        val updatedTask = Task(
            id = task.id,
            boardId = task.boardId,
            columnId = task.columnId,
            title = title ?: task.title,
            description = description ?: task.description,
            assignee = assignee ?: task.assignee,
            priority = priority ?: task.priority,
            status = status ?: task.status,
            dueDate = if (dueDate != null) dueDate.toString() else task.dueDate,
            tags = tags ?: task.tags,
            position = task.position,
            activityLog = task.activityLog,
            createdAt = task.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        val savedTask = taskRepository.save(updatedTask)
        
        // Log activity
        activityLogService.logActivity(
            boardId = task.boardId,
            action = "TASK_UPDATED",
            description = "Task '${savedTask.title}' was updated in column '${column.title}'",
            taskId = taskId,
            columnId = task.columnId
        )
        
        return savedTask
    }

    fun deleteTask(taskId: String): Boolean {
        val task = taskRepository.findById(taskId).orElse(null) ?: return false
        val column = columnService.getColumnById(task.columnId) ?: return false
        val board = boardService.getBoardById(task.boardId) ?: return false
        
        taskRepository.deleteById(taskId)
        
        // Log activity
        activityLogService.logActivity(
            boardId = task.boardId,
            action = "TASK_DELETED",
            description = "Task '${task.title}' was deleted from column '${column.title}'",
            taskId = taskId,
            columnId = task.columnId
        )
        
        return true
    }

    fun moveTask(
        taskId: String,
        targetColumnId: String,
        newPosition: Int?
    ): Task? {
        val task = taskRepository.findById(taskId).orElse(null) ?: return null
        val sourceColumn = columnService.getColumnById(task.columnId) ?: return null
        val targetColumn = columnService.getColumnById(targetColumnId) ?: return null
        val board = boardService.getBoardById(task.boardId) ?: return null
        
        // Moving to a different column proceeds without workflow checks
        
        val updatedTask = Task(
            id = task.id,
            boardId = task.boardId,
            columnId = targetColumnId,
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            priority = task.priority,
            status = task.status,
            dueDate = task.dueDate,
            tags = task.tags,
            position = newPosition ?: (taskRepository.countByColumnId(targetColumnId).toInt()),
            activityLog = task.activityLog,
            createdAt = task.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        val savedTask = taskRepository.save(updatedTask)
        
        // Log activity
        activityLogService.logActivity(
            boardId = task.boardId,
            action = "TASK_MOVED",
            description = "Task '${savedTask.title}' was moved from '${sourceColumn.title}' to '${targetColumn.title}'",
            taskId = taskId,
            columnId = targetColumnId
        )
        
        return savedTask
    }

    fun updateTaskPosition(taskId: String, newPosition: Int): Task? {
        val task = taskRepository.findById(taskId).orElse(null) ?: return null
        val updatedTask = Task(
            id = task.id,
            boardId = task.boardId,
            columnId = task.columnId,
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            priority = task.priority,
            status = task.status,
            dueDate = task.dueDate,
            tags = task.tags,
            position = newPosition,
            activityLog = task.activityLog,
            createdAt = task.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        return taskRepository.save(updatedTask)
    }

}
