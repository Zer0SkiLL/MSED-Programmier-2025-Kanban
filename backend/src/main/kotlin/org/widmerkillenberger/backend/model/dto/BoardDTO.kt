package org.widmerkillenberger.backend.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateBoardRequest(
    @field:NotBlank(message = "Board name is required")
    val name: String
)

data class UpdateBoardRequest(
    @field:NotBlank(message = "Board name is required")
    val name: String
)

data class BoardResponse(
    val id: String,
    val name: String,
    val columns: List<ColumnResponse>,
    val createdAt: String,
    val updatedAt: String
)

data class MoveTaskRequest(
    @field:NotBlank(message = "Target column ID is required")
    val targetColumnId: String,
    val position: Int? = null
)
