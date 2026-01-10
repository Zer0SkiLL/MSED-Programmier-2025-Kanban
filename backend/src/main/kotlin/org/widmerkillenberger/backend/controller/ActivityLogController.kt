package org.widmerkillenberger.backend.controller

import org.widmerkillenberger.backend.model.dto.ActivityLogDTO
import org.widmerkillenberger.backend.service.ActivityLogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/activity-logs")
class ActivityLogController(
    private val activityLogService: ActivityLogService
) {

    @GetMapping("/board/{boardId}")
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

    @GetMapping("/task/{taskId}")
    fun getLogsByTaskId(@PathVariable taskId: String): ResponseEntity<List<ActivityLogDTO>> {
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
