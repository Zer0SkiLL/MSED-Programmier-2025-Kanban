package org.widmerkillenberger.backend.service

import org.widmerkillenberger.backend.model.entity.Column
import org.widmerkillenberger.backend.repository.ColumnRepository
import org.springframework.stereotype.Service

@Service
class ColumnService(
    private val columnRepository: ColumnRepository,
    private val boardService: BoardService,
    private val activityLogService: ActivityLogService
) {

    fun getColumnById(columnId: String): Column? {
        return columnRepository.findById(columnId).orElse(null)
    }

    fun getColumnsByBoardId(boardId: String): List<Column> {
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId)
    }

    fun createColumn(
        boardId: String,
        title: String,
        description: String?,
        color: String?,
        position: Int?
    ): Column {
        val board = boardService.getBoardById(boardId)
            ?: throw IllegalArgumentException("Board not found with id: $boardId")
        
        val column = Column(
            id = null,
            boardId = boardId,
            title = title,
            description = description,
            color = color ?: "#3b82f6",
            position = position ?: (columnRepository.countByBoardId(boardId).toInt()),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val savedColumn = columnRepository.save(column)
        
        // Log activity
        activityLogService.logActivity(
            boardId = boardId,
            action = "COLUMN_CREATED",
            description = "Column '${title}' was created in board '${board.name}'",
            columnId = savedColumn.id
        )
        
        return savedColumn
    }

    fun updateColumn(
        columnId: String,
        title: String?,
        description: String?,
        color: String?,
        position: Int?
    ): Column? {
        val column = columnRepository.findById(columnId).orElse(null) ?: return null
        val board = boardService.getBoardById(column.boardId) ?: return null
        
        val updatedColumn = Column(
            id = column.id,
            boardId = column.boardId,
            title = title ?: column.title,
            description = description ?: column.description,
            color = color ?: column.color,
            position = position ?: column.position,
            tasks = column.tasks,
            createdAt = column.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        val savedColumn = columnRepository.save(updatedColumn)
        
        // Log activity
        activityLogService.logActivity(
            boardId = column.boardId,
            action = "COLUMN_UPDATED",
            description = "Column '${savedColumn.title}' was updated in board '${board.name}'",
            columnId = columnId
        )
        
        return savedColumn
    }

    fun deleteColumn(columnId: String): Boolean {
        val column = columnRepository.findById(columnId).orElse(null) ?: return false
        val board = boardService.getBoardById(column.boardId) ?: return false
        
        columnRepository.deleteById(columnId)
        
        // Log activity
        activityLogService.logActivity(
            boardId = column.boardId,
            action = "COLUMN_DELETED",
            description = "Column '${column.title}' was deleted from board '${board.name}'",
            columnId = columnId
        )
        
        return true
    }

    fun updateColumnPosition(columnId: String, newPosition: Int): Column? {
        val column = columnRepository.findById(columnId).orElse(null) ?: return null
        val updatedColumn = Column(
            id = column.id,
            boardId = column.boardId,
            title = column.title,
            description = column.description,
            color = column.color,
            position = newPosition,
            tasks = column.tasks,
            createdAt = column.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        return columnRepository.save(updatedColumn)
    }
}
