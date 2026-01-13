package org.widmerkillenberger.backend.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "tasks")
data class Task(
    @Id
    val id: String? = null,
    val boardId: String,
    val columnId: String,
    val title: String,
    val description: String? = null,
    val priority: String? = null,
    val assignee: String? = null,
    val status: String? = null,
    val dueDate: String? = null,
    val tags: List<String>? = null,
    val position: Int = 0,
    val activityLog: List<ActivityLog>? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)