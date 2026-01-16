package org.widmerkillenberger.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.widmerkillenberger.backend.model.entity.ActivityLog
import org.widmerkillenberger.backend.service.ActivityLogService

@WebMvcTest(ActivityLogController::class)
class ActivityLogControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var activityLogService: ActivityLogService

    @Test
    fun `getLogsByBoardId returns 200 with list`() {
        every { activityLogService.getLogsByBoardId("b1") } returns listOf(
            ActivityLog(
                id = "l1",
                boardId = "b1",
                taskId = "t1",
                action = "CREATED",
                description = "Task created",
                timestamp = 1,
                user = "system"
            ),
            ActivityLog(
                id = "l2",
                boardId = "b1",
                taskId = null,
                action = "COLUMN_CREATED",
                description = "Column created",
                timestamp = 2,
                user = "system"
            )
        )

        mockMvc.perform(get("/api/activity-logs/board/b1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].id").value("l1"))
            .andExpect(jsonPath("$[0].boardId").value("b1"))
            .andExpect(jsonPath("$[0].action").value("CREATED"))
    }

    @Test
    fun `getLogsByTaskId returns 200 with list`() {
        every { activityLogService.getLogsByTaskId("t1") } returns listOf(
            ActivityLog(
                id = "l1",
                boardId = "b1",
                taskId = "t1",
                action = "UPDATED",
                description = "Task updated",
                timestamp = 10,
                user = "system"
            )
        )

        mockMvc.perform(get("/api/activity-logs/task/t1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].taskId").value("t1"))
    }
}
