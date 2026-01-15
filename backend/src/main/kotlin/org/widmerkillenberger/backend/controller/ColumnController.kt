package org.widmerkillenberger.backend.controller

import jakarta.validation.Valid
import org.widmerkillenberger.backend.model.dto.*
import org.widmerkillenberger.backend.service.ColumnService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/boards/{boardId}/columns")
class ColumnController(
    private val columnService: ColumnService
) {

    @GetMapping
    fun getColumnsByBoardId(@PathVariable boardId: String): ResponseEntity<List<ColumnResponse>> {
        val columns = columnService.getColumnsByBoardId(boardId)
        val responses = columns.map { column ->
            ColumnResponse(
                id = column.id ?: "",
                boardId = column.boardId,
                name = column.title,
                position = column.position,
                tasks = emptyList()
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/{columnId}")
    fun getColumnById(
        @PathVariable boardId: String,
        @PathVariable columnId: String
    ): ResponseEntity<ColumnResponse> {
        val column = columnService.getColumnById(columnId)
        return if (column != null) {
            val response = ColumnResponse(
                id = column.id ?: "",
                boardId = column.boardId,
                name = column.title,
                position = column.position,
                tasks = emptyList()
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createColumn(
        @PathVariable boardId: String,
        @Valid @RequestBody request: CreateColumnRequest
    ): ResponseEntity<ColumnResponse> {
        val column = columnService.createColumn(
            boardId = boardId,
            title = request.title,
            description = null,
            color = null,
            position = null,
            workflowRules = null
        )
        val response = ColumnResponse(
            id = column.id ?: "",
            boardId = column.boardId,
            name = column.title,
            position = column.position,
            tasks = emptyList()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{columnId}")
    fun updateColumn(
        @PathVariable boardId: String,
        @PathVariable columnId: String,
        @Valid @RequestBody request: UpdateColumnRequest
    ): ResponseEntity<ColumnResponse> {
        val column = columnService.updateColumn(
            columnId = columnId,
            title = request.title,
            description = null,
            color = null,
            position = null,
            workflowRules = null
        )
        return if (column != null) {
            val response = ColumnResponse(
                id = column.id ?: "",
                boardId = column.boardId,
                name = column.title,
                position = column.position,
                tasks = emptyList()
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{columnId}")
    fun deleteColumn(
        @PathVariable boardId: String,
        @PathVariable columnId: String
    ): ResponseEntity<Void> {
        val deleted = columnService.deleteColumn(columnId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
