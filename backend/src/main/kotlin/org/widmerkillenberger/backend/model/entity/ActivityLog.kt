package org.widmerkillenberger.backend.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "activity_logs")
data class ActivityLog(
    @Id
    val id: String? = null,
    val taskId: String? = null,
    val boardId: String? = null,
    val action: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val user: String
)