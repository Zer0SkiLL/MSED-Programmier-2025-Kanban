package org.widmerkillenberger.backend.repository

import org.widmerkillenberger.backend.model.entity.ActivityLog
import org.springframework.data.mongodb.repository.MongoRepository

interface ActivityLogRepository : MongoRepository<ActivityLog, String> {
    fun findByBoardId(boardId: String): List<ActivityLog>
    fun findByTaskId(taskId: String): List<ActivityLog>
}
