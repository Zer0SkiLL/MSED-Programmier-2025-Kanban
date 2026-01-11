package org.widmerkillenberger.backend.repository

import org.widmerkillenberger.backend.model.entity.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, String> {
    fun findByColumnIdOrderByPositionAsc(columnId: String): List<Task>
    fun findByBoardIdOrderByCreatedAtDesc(boardId: String): List<Task>
    fun countByColumnId(columnId: String): Long
}