package org.widmerkillenberger.backend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.widmerkillenberger.backend.model.entity.Board
import org.widmerkillenberger.backend.model.entity.Column
import org.widmerkillenberger.backend.repository.ColumnRepository
import java.util.*

class ColumnServiceTest {

    private val columnRepository: ColumnRepository = mockk(relaxed = true)
    private val boardService: BoardService = mockk(relaxed = true)
    private val activityLogService: ActivityLogService = mockk(relaxed = true)

    private val service = ColumnService(columnRepository, boardService, activityLogService)

    @Test
    fun `getColumnById delegates to repository`() {
        every { columnRepository.findById("c1") } returns Optional.of(Column(id = "c1", boardId = "b1", title = "Col"))

        val c = service.getColumnById("c1")
        assertEquals("c1", c?.id)
        verify { columnRepository.findById("c1") }
    }

    @Test
    fun `getColumnsByBoardId delegates and sorts`() {
        every { columnRepository.findByBoardIdOrderByPositionAsc("b1") } returns listOf(
            Column(id = "c1", boardId = "b1", title = "A", position = 0),
            Column(id = "c2", boardId = "b1", title = "B", position = 1)
        )

        val list = service.getColumnsByBoardId("b1")
        assertEquals(2, list.size)
        verify { columnRepository.findByBoardIdOrderByPositionAsc("b1") }
    }

    @Test
    fun `createColumn uses default position and logs`() {
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { columnRepository.countByBoardId("b1") } returns 2L
        every { columnRepository.save(any()) } answers { firstArg<Column>().copy(id = "newC") }
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val created = service.createColumn(
            boardId = "b1",
            title = "New Col",
            description = null,
            color = null,
            position = null
        )

        assertEquals("newC", created.id)
        assertEquals(2, created.position)
        verify {
            activityLogService.logActivity(
                boardId = "b1",
                action = "COLUMN_CREATED",
                description = any()
            )
        }
    }

    @Test
    fun `createColumn throws when board missing`() {
        every { boardService.getBoardById("b1") } returns null

        val ex = assertThrows(IllegalArgumentException::class.java) {
            service.createColumn("b1", "Title", null, null, null)
        }
        assertTrue(ex.message!!.contains("Board not found"))
        verify(exactly = 0) { columnRepository.save(any()) }
    }

    @Test
    fun `updateColumn updates and logs`() {
        every { columnRepository.findById("c1") } returns Optional.of(
            Column(id = "c1", boardId = "b1", title = "Old", position = 0)
        )
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { columnRepository.save(any()) } answers { firstArg() }
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val updated = service.updateColumn(
            "c1",
            title = "New",
            description = null,
            color = null,
            position = 1
        )

        assertNotNull(updated)
        assertEquals("New", updated?.title)
        assertEquals(1, updated?.position)
        verify { activityLogService.logActivity("b1", "COLUMN_UPDATED", any()) }
    }

    @Test
    fun `updateColumn returns null when not found`() {
        every { columnRepository.findById("missing") } returns Optional.empty()

        val result = service.updateColumn("missing", null, null, null, null)
        assertNull(result)
        verify(exactly = 0) { columnRepository.save(any()) }
    }

    @Test
    fun `deleteColumn deletes and logs`() {
        every { columnRepository.findById("c1") } returns Optional.of(Column(id = "c1", boardId = "b1", title = "Col"))
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val ok = service.deleteColumn("c1")

        assertTrue(ok)
        verify { columnRepository.deleteById("c1") }
        verify { activityLogService.logActivity("b1", "COLUMN_DELETED", any()) }
    }

    @Test
    fun `deleteColumn returns false when not found`() {
        every { columnRepository.findById("missing") } returns Optional.empty()

        val ok = service.deleteColumn("missing")
        assertFalse(ok)
        verify(exactly = 0) { columnRepository.deleteById(any()) }
    }

    @Test
    fun `updateColumnPosition persists new position`() {
        every { columnRepository.findById("c1") } returns Optional.of(
            Column(id = "c1", boardId = "b1", title = "Col", position = 0)
        )
        every { columnRepository.save(any()) } answers { firstArg() }

        val updated = service.updateColumnPosition("c1", 5)
        assertEquals(5, updated?.position)
        verify { columnRepository.save(any()) }
    }
}
