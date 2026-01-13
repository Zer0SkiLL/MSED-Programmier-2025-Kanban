package org.widmerkillenberger.backend.repository

import org.widmerkillenberger.backend.model.entity.Column
import org.springframework.data.mongodb.repository.MongoRepository

interface ColumnRepository : MongoRepository<Column, String> {
    fun findByBoardIdOrderByPositionAsc(boardId: String): List<Column>
    fun countByBoardId(boardId: String): Long
}