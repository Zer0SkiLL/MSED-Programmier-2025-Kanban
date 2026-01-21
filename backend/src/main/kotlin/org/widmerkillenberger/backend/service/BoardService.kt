package org.widmerkillenberger.backend.service

import org.widmerkillenberger.backend.model.entity.Board
import org.widmerkillenberger.backend.repository.BoardRepository
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {

    fun getAllBoards(): List<Board> {
        return boardRepository.findAll()
    }

    fun getBoardById(boardId: String): Board? {
        return boardRepository.findById(boardId).orElse(null)
    }

    fun createBoard(name: String, description: String?): Board {
        val board = Board(
            id = null,
            name = name,
            description = description,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return boardRepository.save(board)
    }

    fun updateBoard(boardId: String, name: String, description: String?): Board? {
        val board = boardRepository.findById(boardId).orElse(null) ?: return null
        val updatedBoard = Board(
            id = board.id,
            name = name,
            description = description,
            activityLog = board.activityLog,
            createdAt = board.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        return boardRepository.save(updatedBoard)
    }

    fun deleteBoard(boardId: String): Boolean {
        val board = boardRepository.findById(boardId).orElse(null) ?: return false
        boardRepository.deleteById(boardId)
        return true
    }
}
