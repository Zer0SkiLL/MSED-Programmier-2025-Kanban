package org.widmerkillenberger.backend.controller

import jakarta.validation.Valid
import org.widmerkillenberger.backend.model.dto.*
import org.widmerkillenberger.backend.service.BoardService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/boards")
class BoardController(
    private val boardService: BoardService
) {

    @GetMapping
    fun getAllBoards(): ResponseEntity<List<BoardResponse>> {
        val boards = boardService.getAllBoards()
        val responses = boards.map {board ->
            BoardResponse(
                id = board.id ?: "",
                name = board.name,
                columns = emptyList(),
                createdAt = board.createdAt?.toString() ?: "",
                updatedAt = board.updatedAt?.toString() ?: ""
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/{id}")
    fun getBoardById(@PathVariable id: String): ResponseEntity<BoardResponse> {
        val board = boardService.getBoardById(id)
        return if (board != null) {
            val response = BoardResponse(
                id = board.id ?: "",
                name = board.name,
                columns = emptyList(),
                createdAt = board.createdAt?.toString() ?: "",
                updatedAt = board.updatedAt?.toString() ?: ""
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createBoard(@Valid @RequestBody request: CreateBoardRequest): ResponseEntity<BoardResponse> {
        val board = boardService.createBoard(
            name = request.name,
            description = null
        )
        val response =  BoardResponse(
            id = board.id ?: "",
            name = board.name,
            columns = emptyList(),
            createdAt = board.createdAt?.toString() ?: "",
            updatedAt = board.updatedAt?.toString() ?: ""
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun updateBoard(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateBoardRequest
    ): ResponseEntity<BoardResponse> {
        val board = boardService.updateBoard(id, request.name, null)
        return if (board != null) {
            val response = BoardResponse(
                id = board.id ?: "",
                name = board.name,
                columns = emptyList(),
                createdAt = board.createdAt?.toString() ?: "",
                updatedAt = board.updatedAt?.toString() ?: ""
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBoard(@PathVariable id: String): ResponseEntity<Void> {
        val deleted = boardService.deleteBoard(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
