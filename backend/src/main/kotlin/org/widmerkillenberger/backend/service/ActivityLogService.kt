package org.widmerkillenberger.backend.service

import org.widmerkillenberger.backend.model.entity.ActivityLog
import org.widmerkillenberger.backend.repository.ActivityLogRepository
import org.springframework.stereotype.Service

@Service
class ActivityLogService(
    private val activityLogRepository: ActivityLogRepository
) {
    fun createLog(taskId: String?, boardId: String?, action: String, description: String): ActivityLog {
        val log = ActivityLog(
            taskId = taskId,
            boardId = boardId,
            action = action,
            description = description
        )
        return activityLogRepository.save(log)
    }

    fun logActivity(
        boardId: String,
        action: String,
        description: String,
        taskId: String? = null
    ): ActivityLog {
        val log = ActivityLog(
            id = null,
            boardId = boardId,
            taskId = taskId,
            action = action,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        return activityLogRepository.save(log)
    }

    fun getLogsByBoardId(boardId: String): List<ActivityLog> {
        return activityLogRepository.findByBoardId(boardId)
    }

    fun getLogsByTaskId(taskId: String): List<ActivityLog> {
        return activityLogRepository.findByTaskId(taskId)
    }
}
