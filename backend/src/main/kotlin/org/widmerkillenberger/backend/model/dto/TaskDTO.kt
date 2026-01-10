package org.widmerkillenberger.backend.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateTaskRequest(
    @field:NotBlank(message = "Task title is required")
    val title: String,
    val description: String? = null,
    val assignee: String? = null,
    val priority: String? = "medium",
    val dueDate: String? = null,
    val tags: List<String>? = null
)

data class UpdateTaskRequest(
    @field:NotBlank(message = "Task title is required")
    val title: String,
    val description: String? = null,
    val assignee: String? = null,
    val priority: String? = null,
    val dueDate: String? = null,
    val tags: List<String>? = null
)

data class TaskResponse(
    val id: String,
    val columnId: String,
    val title: String,
    val description: String?,
    val assignee: String?,
    val priority: String,
    val tags: List<String>,
    val dueDate: String?,
    val createdAt: String,
    val updatedAt: String
)
