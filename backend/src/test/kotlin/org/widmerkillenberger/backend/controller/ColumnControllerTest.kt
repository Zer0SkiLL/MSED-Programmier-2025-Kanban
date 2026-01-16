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
import org.widmerkillenberger.backend.model.entity.Column
import org.widmerkillenberger.backend.service.ColumnService

@WebMvcTest(ColumnController::class)
class ColumnControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var columnService: ColumnService

    @Test
    fun `getColumnsByBoardId returns 200 with list`() {
        every { columnService.getColumnsByBoardId("b1") } returns listOf(
            Column(id = "c1", boardId = "b1", title = "Todo", position = 0, createdAt = 1, updatedAt = 2),
            Column(id = "c2", boardId = "b1", title = "Doing", position = 1, createdAt = 1, updatedAt = 2)
        )

        mockMvc.perform(get("/api/boards/b1/columns"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].id").value("c1"))
            .andExpect(jsonPath("$[0].boardId").value("b1"))
            .andExpect(jsonPath("$[0].name").value("Todo"))
    }

    @Test
    fun `getColumnById returns 200 when found`() {
        every { columnService.getColumnById("c1") } returns Column(
            id = "c1",
            boardId = "b1",
            title = "Todo",
            position = 0
        )

        mockMvc.perform(get("/api/boards/b1/columns/c1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("c1"))
            .andExpect(jsonPath("$.name").value("Todo"))
    }

    @Test
    fun `getColumnById returns 404 when not found`() {
        every { columnService.getColumnById("missing") } returns null

        mockMvc.perform(get("/api/boards/b1/columns/missing"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createColumn returns 201`() {
        every { columnService.createColumn("b1", "New Col", null, null, null, null) } returns Column(
            id = "new", boardId = "b1", title = "New Col", position = 2
        )

        val body = """
            {"title":"New Col"}
        """.trimIndent()

        mockMvc.perform(post("/api/boards/b1/columns").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("new"))
            .andExpect(jsonPath("$.boardId").value("b1"))
            .andExpect(jsonPath("$.name").value("New Col"))
    }

    @Test
    fun `updateColumn returns 200 when found`() {
        every { columnService.updateColumn("c1", "Renamed", null, null, null, null) } returns Column(
            id = "c1", boardId = "b1", title = "Renamed", position = 0
        )

        val body = """
            {"title":"Renamed"}
        """.trimIndent()

        mockMvc.perform(put("/api/boards/b1/columns/c1").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("c1"))
            .andExpect(jsonPath("$.name").value("Renamed"))
    }

    @Test
    fun `updateColumn returns 404 when not found`() {
        every { columnService.updateColumn("missing", any(), any(), any(), any(), any()) } returns null

        val body = """
            {"title":"Name"}
        """.trimIndent()

        mockMvc.perform(put("/api/boards/b1/columns/missing").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteColumn returns 204 when success`() {
        every { columnService.deleteColumn("c1") } returns true

        mockMvc.perform(delete("/api/boards/b1/columns/c1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteColumn returns 404 when not found`() {
        every { columnService.deleteColumn("missing") } returns false

        mockMvc.perform(delete("/api/boards/b1/columns/missing"))
            .andExpect(status().isNotFound)
    }
}
