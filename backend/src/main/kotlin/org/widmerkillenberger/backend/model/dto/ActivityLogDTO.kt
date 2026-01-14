package org.widmerkillenberger.backend.model.dto

data class ActivityLogDTO(
    val id: String?,
    val boardId: String,
    val taskId: String?,
    val action: String,
    val description: String,
    val timestamp: Long
)

data class ActivityLogResponse(
    val id: String,
    val boardId: String,
    val taskId: String?,
    val action: String,
    val description: String,
    val timestamp: Long
)
