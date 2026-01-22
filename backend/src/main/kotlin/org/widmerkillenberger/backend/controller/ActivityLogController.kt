package org.widmerkillenberger.backend.controller

import org.widmerkillenberger.backend.model.dto.ActivityLogDTO
import org.widmerkillenberger.backend.service.ActivityLogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/boards/{boardId}")
class ActivityLogController(
    private val activityLogService: ActivityLogService
) {

    @GetMapping("/activity-logs")
    fun getLogsByBoardId(@PathVariable boardId: String): ResponseEntity<List<ActivityLogDTO>> {
        val logs = activityLogService.getLogsByBoardId(boardId)
        val responses = logs.map { log ->
            ActivityLogDTO(
                id = log.id,
                boardId = log.boardId ?: "",
                taskId = log.taskId,
                action = log.action,
                description = log.description,
                timestamp = log.timestamp
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/tasks/{taskId}/activity-logs")
    fun getLogsByTaskId(
        @PathVariable taskId: String
    ): ResponseEntity<List<ActivityLogDTO>> {
        val logs = activityLogService.getLogsByTaskId(taskId)
        val responses = logs.map { log ->
            ActivityLogDTO(
                id = log.id,
                boardId = log.boardId ?: "",
                taskId = log.taskId,
                action = log.action,
                description = log.description,
                timestamp = log.timestamp
            )
        }
        return ResponseEntity.ok(responses)
    }
}
