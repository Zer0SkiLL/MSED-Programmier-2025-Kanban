package org.widmerkillenberger.backend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.widmerkillenberger.backend.model.entity.Board
import org.widmerkillenberger.backend.model.entity.Column
import org.widmerkillenberger.backend.model.entity.Task
import org.widmerkillenberger.backend.repository.TaskRepository
import java.util.*

class TaskServiceTest {

    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private val columnService: ColumnService = mockk(relaxed = true)
    private val boardService: BoardService = mockk(relaxed = true)
    private val activityLogService: ActivityLogService = mockk(relaxed = true)

    private val service = TaskService(taskRepository, columnService, boardService, activityLogService)

    @Test
    fun `getTaskById delegates to repository`() {
        every { taskRepository.findById("t1") } returns Optional.of(
            Task(id = "t1", boardId = "b1", columnId = "c1", title = "T1")
        )

        val t = service.getTaskById("t1")
        assertEquals("t1", t?.id)
        verify { taskRepository.findById("t1") }
    }

    @Test
    fun `get tasks by column and board delegate`() {
        every { taskRepository.findByColumnIdOrderByPositionAsc("c1") } returns emptyList()
        every { taskRepository.findByBoardIdOrderByCreatedAtDesc("b1") } returns emptyList()

        service.getTasksByColumnId("c1")
        service.getTasksByBoardId("b1")

        verify { taskRepository.findByColumnIdOrderByPositionAsc("c1") }
        verify { taskRepository.findByBoardIdOrderByCreatedAtDesc("b1") }
    }

    @Test
    fun `createTask uses defaults and logs`() {
        every { columnService.getColumnById("c1") } returns Column(id = "c1", boardId = "b1", title = "Todo")
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { taskRepository.countByColumnId("c1") } returns 3L
        every { taskRepository.save(any()) } answers { firstArg<Task>().copy(id = "newT") }
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val created = service.createTask(
            boardId = "b1",
            columnId = "c1",
            title = "New T",
            description = null,
            assignee = null,
            priority = null,
            dueDate = null,
            tags = null,
            position = null
        )

        assertEquals("newT", created.id)
        assertEquals(3, created.position)
        assertEquals("medium", created.priority)
        verify { activityLogService.logActivity("b1", "TASK_CREATED", any(), created.id) }
    }

    @Test
    fun `createTask throws when board or column missing`() {
        every { columnService.getColumnById("c1") } returns null

        assertThrows(IllegalArgumentException::class.java) {
            service.createTask("b1", "c1", "T", null, null, null, null, null, null)
        }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun `updateTask updates fields and logs`() {
        every { taskRepository.findById("t1") } returns Optional.of(
            Task(id = "t1", boardId = "b1", columnId = "c1", title = "Old", createdAt = 1L, updatedAt = 1L)
        )
        every { columnService.getColumnById("c1") } returns Column(id = "c1", boardId = "b1", title = "Todo")
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { taskRepository.save(any()) } answers { firstArg() }
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val updated = service.updateTask(
            taskId = "t1",
            title = "New",
            description = "D",
            assignee = "A",
            priority = "high",
            status = "in_progress",
            dueDate = 123,
            tags = listOf("x")
        )

        assertNotNull(updated)
        assertEquals("New", updated?.title)
        assertEquals("D", updated?.description)
        assertEquals("A", updated?.assignee)
        assertEquals("high", updated?.priority)
        assertEquals("in_progress", updated?.status)
        assertEquals("123", updated?.dueDate)
        assertEquals(listOf("x"), updated?.tags)
        verify { activityLogService.logActivity("b1", "TASK_UPDATED", any(), "t1") }
    }

    @Test
    fun `updateTask returns null when task missing or deps missing`() {
        every { taskRepository.findById("missing") } returns Optional.empty()
        val r1 = service.updateTask("missing", null, null, null, null, null, null, null)
        assertNull(r1)

        every { taskRepository.findById("t1") } returns Optional.of(
            Task(
                id = "t1",
                boardId = "b1",
                columnId = "c1",
                title = "Old"
            )
        )
        every { columnService.getColumnById("c1") } returns null
        val r2 = service.updateTask("t1", null, null, null, null, null, null, null)
        assertNull(r2)
    }

    @Test
    fun `deleteTask deletes and logs`() {
        every { taskRepository.findById("t1") } returns Optional.of(
            Task(
                id = "t1",
                boardId = "b1",
                columnId = "c1",
                title = "T"
            )
        )
        every { columnService.getColumnById("c1") } returns Column(id = "c1", boardId = "b1", title = "Todo")
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "Board")
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val ok = service.deleteTask("t1")
        assertTrue(ok)
        verify { taskRepository.deleteById("t1") }
        verify { activityLogService.logActivity("b1", "TASK_DELETED", any(), "t1") }
    }

    @Test
    fun `deleteTask returns false when not found`() {
        every { taskRepository.findById("missing") } returns Optional.empty()
        val ok = service.deleteTask("missing")
        assertFalse(ok)
        verify(exactly = 0) { taskRepository.deleteById(any()) }
    }

    @Test
    fun `moveTask moves, sets position and logs`() {
        val existing = Task(id = "t1", boardId = "b1", columnId = "c1", title = "T", position = 0, assignee = "A")
        every { taskRepository.findById("t1") } returns Optional.of(existing)
        every { columnService.getColumnById("c1") } returns Column(id = "c1", boardId = "b1", title = "Todo")
        every { columnService.getColumnById("c2") } returns Column(
            id = "c2",
            boardId = "b1",
            title = "Doing"
        )
        every { boardService.getBoardById("b1") } returns Board(id = "b1", name = "B")
        every { taskRepository.countByColumnId("c2") } returns 5L
        every { taskRepository.save(any()) } answers { firstArg<Task>() }
        every { activityLogService.logActivity(any(), any(), any(), any()) } returns mockk()

        val moved = service.moveTask("t1", "c2", null)

        assertNotNull(moved)
        assertEquals("c2", moved?.columnId)
        assertEquals(5, moved?.position)
        verify { activityLogService.logActivity("b1", "TASK_MOVED", any(), "t1") }
    }

    @Test
    fun `updateTaskPosition updates persistence`() {
        every { taskRepository.findById("t1") } returns Optional.of(
            Task(
                id = "t1",
                boardId = "b1",
                columnId = "c1",
                title = "T",
                position = 0
            )
        )
        every { taskRepository.save(any()) } answers { firstArg() }

        val updated = service.updateTaskPosition("t1", 7)
        assertEquals(7, updated?.position)
        verify { taskRepository.save(any()) }
    }
}
