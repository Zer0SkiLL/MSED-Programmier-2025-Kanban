package org.widmerkillenberger.backend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.widmerkillenberger.backend.model.entity.ActivityLog
import org.widmerkillenberger.backend.repository.ActivityLogRepository

class ActivityLogServiceTest {

    private val repo: ActivityLogRepository = mockk(relaxed = true)
    private val service = ActivityLogService(repo)

    @Test
    fun `createLog saves custom log`() {
        every { repo.save(any()) } answers { firstArg() }

        val saved = service.createLog(taskId = "t1", boardId = "b1", action = "X", description = "desc")

        assertEquals("t1", saved.taskId)
        assertEquals("b1", saved.boardId)
        assertEquals("X", saved.action)
        assertEquals("desc", saved.description)
        verify { repo.save(any()) }
    }

    @Test
    fun `logActivity saves system log`() {
        every { repo.save(any()) } answers { firstArg<ActivityLog>().copy(id = "id1") }

        val saved =
            service.logActivity(boardId = "b1", action = "ACT", description = "d", taskId = "t1")

        assertEquals("id1", saved.id)
        assertEquals("b1", saved.boardId)
        assertEquals("t1", saved.taskId)
        assertEquals("ACT", saved.action)
        assertEquals("d", saved.description)
        verify {
            repo.save(any())
        }
    }

    @Test
    fun `get logs delegates to repo`() {
        every { repo.findByBoardId("b1") } returns emptyList()
        every { repo.findByTaskId("t1") } returns emptyList()

        service.getLogsByBoardId("b1")
        service.getLogsByTaskId("t1")

        verify { repo.findByBoardId("b1") }
        verify { repo.findByTaskId("t1") }
    }
}
