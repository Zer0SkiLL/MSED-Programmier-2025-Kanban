package org.widmerkillenberger.backend.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "boards")
data class Board(
    @Id
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val columns: List<Column> = emptyList(),
    val activityLog: List<ActivityLog>? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)
