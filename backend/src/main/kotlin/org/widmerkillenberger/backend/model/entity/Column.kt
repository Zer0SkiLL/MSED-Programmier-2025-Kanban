package org.widmerkillenberger.backend.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "columns")
data class Column(
    @Id
    val id: String? = null,
    val boardId: String,
    val title: String,
    val description: String? = null,
    val color: String? = null,
    val position: Int = 0,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)
