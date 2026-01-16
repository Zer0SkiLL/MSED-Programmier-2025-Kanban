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
import org.widmerkillenberger.backend.model.entity.Board
import org.widmerkillenberger.backend.service.BoardService

@WebMvcTest(BoardController::class)
class BoardControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var boardService: BoardService

    @Test
    fun `getAllBoards returns 200 with list`() {
        every { boardService.getAllBoards() } returns listOf(
            Board(id = "b1", name = "Board 1", createdAt = 1, updatedAt = 2),
            Board(id = "b2", name = "Board 2", createdAt = 3, updatedAt = 4)
        )

        mockMvc.perform(get("/api/boards"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].id").value("b1"))
            .andExpect(jsonPath("$[0].name").value("Board 1"))
    }

    @Test
    fun `getBoardById returns 200 when found`() {
        every { boardService.getBoardById("b1") } returns Board(
            id = "b1",
            name = "Board 1",
            createdAt = 1,
            updatedAt = 2
        )

        mockMvc.perform(get("/api/boards/b1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("b1"))
            .andExpect(jsonPath("$.name").value("Board 1"))
    }

    @Test
    fun `getBoardById returns 404 when not found`() {
        every { boardService.getBoardById("missing") } returns null

        mockMvc.perform(get("/api/boards/missing"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createBoard returns 201`() {
        every { boardService.createBoard("New Board", null) } returns Board(
            id = "new",
            name = "New Board",
            createdAt = 10,
            updatedAt = 10
        )

        val body = """
            {"name":"New Board"}
        """.trimIndent()

        mockMvc.perform(post("/api/boards").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("new"))
            .andExpect(jsonPath("$.name").value("New Board"))
    }

    @Test
    fun `updateBoard returns 200 when found`() {
        every { boardService.updateBoard("b1", "Renamed", null) } returns Board(
            id = "b1",
            name = "Renamed",
            createdAt = 1,
            updatedAt = 5
        )

        val body = """
            {"name":"Renamed"}
        """.trimIndent()

        mockMvc.perform(put("/api/boards/b1").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("b1"))
            .andExpect(jsonPath("$.name").value("Renamed"))
    }

    @Test
    fun `updateBoard returns 404 when not found`() {
        every { boardService.updateBoard("missing", any(), any()) } returns null

        val body = """
            {"name":"Name"}
        """.trimIndent()

        mockMvc.perform(put("/api/boards/missing").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteBoard returns 204 when success`() {
        every { boardService.deleteBoard("b1") } returns true

        mockMvc.perform(delete("/api/boards/b1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteBoard returns 404 when not found`() {
        every { boardService.deleteBoard("missing") } returns false

        mockMvc.perform(delete("/api/boards/missing"))
            .andExpect(status().isNotFound)
    }
}
