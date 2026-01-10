package org.widmerkillenberger.backend.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateColumnRequest(
    @field:NotBlank(message = "Column title is required")
    val title: String
)

data class UpdateColumnRequest(
    @field:NotBlank(message = "Column title is required")
    val title: String
)

data class ColumnResponse(
    val id: String,
    val boardId: String,
    val name: String,
    val position: Int,
    val tasks: List<TaskResponse>
)
