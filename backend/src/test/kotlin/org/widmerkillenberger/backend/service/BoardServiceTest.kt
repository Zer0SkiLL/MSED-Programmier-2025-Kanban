package org.widmerkillenberger.backend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.widmerkillenberger.backend.model.entity.Board
import org.widmerkillenberger.backend.repository.BoardRepository
import java.util.*

class BoardServiceTest {

    private val boardRepository: BoardRepository = mockk(relaxed = true)
    private val service = BoardService(boardRepository)

    @Test
    fun `getAllBoards delegates to repository`() {
        every { boardRepository.findAll() } returns listOf(Board(id = "b1", name = "B1"))

        val result = service.getAllBoards()

        assertEquals(1, result.size)
        assertEquals("b1", result[0].id)
        verify(exactly = 1) { boardRepository.findAll() }
    }

    @Test
    fun `getBoardById returns found board`() {
        every { boardRepository.findById("b1") } returns Optional.of(Board(id = "b1", name = "B1"))

        val result = service.getBoardById("b1")

        assertNotNull(result)
        assertEquals("b1", result?.id)
        verify { boardRepository.findById("b1") }
    }

    @Test
    fun `getBoardById returns null when missing`() {
        every { boardRepository.findById("missing") } returns Optional.empty()

        val result = service.getBoardById("missing")

        assertNull(result)
        verify { boardRepository.findById("missing") }
    }

    @Test
    fun `createBoard saves with generated timestamps`() {
        every { boardRepository.save(any()) } answers {
            val arg = firstArg<Board>()
            // emulate DB assigning id
            arg.copy(id = "newId")
        }

        val created = service.createBoard("New Board", null)

        assertEquals("newId", created.id)
        assertEquals("New Board", created.name)
        verify {
            boardRepository.save(withArg { b ->
                assertNull(b.id)
                assertEquals("New Board", b.name)
                assertNotNull(b.createdAt)
                assertNotNull(b.updatedAt)
            })
        }
    }

    @Test
    fun `updateBoard updates name and description when found`() {
        every { boardRepository.findById("b1") } returns Optional.of(
            Board(id = "b1", name = "Old", description = null, createdAt = 1, updatedAt = 1)
        )
        every { boardRepository.save(any()) } answers { firstArg() }

        val updated = service.updateBoard("b1", "New", "Desc")

        assertNotNull(updated)
        assertEquals("b1", updated?.id)
        assertEquals("New", updated?.name)
        assertEquals("Desc", updated?.description)
        assertNotNull(updated?.updatedAt)
        verify { boardRepository.save(any()) }
    }

    @Test
    fun `updateBoard returns null when not found`() {
        every { boardRepository.findById("missing") } returns Optional.empty()

        val updated = service.updateBoard("missing", "Name", null)

        assertNull(updated)
        verify(exactly = 0) { boardRepository.save(any()) }
    }

    @Test
    fun `deleteBoard returns true when existing`() {
        every { boardRepository.findById("b1") } returns Optional.of(Board(id = "b1", name = "B"))
        every { boardRepository.deleteById("b1") } returns Unit

        val result = service.deleteBoard("b1")

        assertTrue(result)
        verify { boardRepository.deleteById("b1") }
    }

    @Test
    fun `deleteBoard returns false when missing`() {
        every { boardRepository.findById("missing") } returns Optional.empty()

        val result = service.deleteBoard("missing")

        assertFalse(result)
        verify(exactly = 0) { boardRepository.deleteById(any()) }
    }
}
