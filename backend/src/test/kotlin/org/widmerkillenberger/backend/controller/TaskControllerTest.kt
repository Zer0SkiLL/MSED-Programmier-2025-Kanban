package org.widmerkillenberger.backend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.widmerkillenberger.backend.model.entity.Task
import org.widmerkillenberger.backend.service.TaskService

@WebMvcTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var taskService: TaskService

    @Test
    fun `getTasksByColumnId returns 200 with list`() {
        every { taskService.getTasksByColumnId("col1") } returns listOf(
            Task(id = "t1", boardId = "b1", columnId = "col1", title = "Task 1", createdAt = 1, updatedAt = 2),
            Task(id = "t2", boardId = "b1", columnId = "col1", title = "Task 2", createdAt = 3, updatedAt = 4)
        )

        mockMvc.perform(get("/api/boards/b1/columns/col1/tasks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].id").value("t1"))
            .andExpect(jsonPath("$[0].columnId").value("col1"))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[0].createdAt").value("1"))
    }

    @Test
    fun `getTaskById returns 200 when found`() {
        every { taskService.getTaskById("t1") } returns Task(
            id = "t1",
            boardId = "b1",
            columnId = "col1",
            title = "Task 1",
            createdAt = 1,
            updatedAt = 2
        )

        mockMvc.perform(get("/api/boards/b1/columns/col1/tasks/t1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("t1"))
            .andExpect(jsonPath("$.title").value("Task 1"))
            .andExpect(jsonPath("$.createdAt").value("1"))
    }

    @Test
    fun `getTaskById returns 404 when not found`() {
        every { taskService.getTaskById("missing") } returns null

        mockMvc.perform(get("/api/boards/b1/columns/col1/tasks/missing"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createTask returns 201`() {
        every {
            taskService.createTask(
                "b1",
                "col1",
                "New Task",
                any(),
                any(),
                any(),
                any(),
                any(),
                null
            )
        } returns Task(
            id = "new", boardId = "b1", columnId = "col1", title = "New Task", createdAt = 10, updatedAt = 10
        )

        val body = """
            {"title":"New Task"}
        """.trimIndent()

        mockMvc.perform(post("/api/boards/b1/columns/col1/tasks").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("new"))
            .andExpect(jsonPath("$.columnId").value("col1"))
            .andExpect(jsonPath("$.title").value("New Task"))
    }

    @Test
    fun `updateTask returns 200 when found`() {
        every { taskService.updateTask("t1", "Renamed", any(), any(), any(), any(), any(), any()) } returns Task(
            id = "t1", boardId = "b1", columnId = "col1", title = "Renamed", createdAt = 1, updatedAt = 5
        )

        val body = """
            {"title":"Renamed"}
        """.trimIndent()

        mockMvc.perform(
            put("/api/boards/b1/columns/col1/tasks/t1").contentType(MediaType.APPLICATION_JSON).content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("t1"))
            .andExpect(jsonPath("$.title").value("Renamed"))
    }

    @Test
    fun `updateTask returns 404 when not found`() {
        every { taskService.updateTask("missing", any(), any(), any(), any(), any(), any(), any()) } returns null

        val body = """
            {"title":"Name"}
        """.trimIndent()

        mockMvc.perform(
            put("/api/boards/b1/columns/col1/tasks/missing").contentType(MediaType.APPLICATION_JSON).content(body)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteTask returns 204 when success`() {
        every { taskService.deleteTask("t1") } returns true

        mockMvc.perform(delete("/api/boards/b1/columns/col1/tasks/t1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteTask returns 404 when not found`() {
        every { taskService.deleteTask("missing") } returns false

        mockMvc.perform(delete("/api/boards/b1/columns/col1/tasks/missing"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `moveTask returns 200 when found`() {
        every { taskService.moveTask("t1", "col2", 1) } returns Task(
            id = "t1", boardId = "b1", columnId = "col2", title = "Task 1", position = 1
        )

        val body = """
            {"targetColumnId":"col2", "position":1}
        """.trimIndent()

        mockMvc.perform(
            patch("/api/boards/b1/columns/col1/tasks/t1/move").contentType(MediaType.APPLICATION_JSON).content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.columnId").value("col2"))

    }

    @Test
    fun `moveTask returns 404 when not found`() {
        every { taskService.moveTask("missing", any(), any()) } returns null

        val body = """
            {"targetColumnId":"col2"}
        """.trimIndent()

        mockMvc.perform(
            patch("/api/boards/b1/columns/col1/tasks/missing/move").contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isNotFound)
    }
}
