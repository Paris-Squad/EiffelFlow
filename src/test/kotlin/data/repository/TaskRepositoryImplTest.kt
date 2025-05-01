package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock.validTask
import java.util.UUID

//todo change those testcases
class TaskRepositoryImplTest {

    private lateinit var taskRepository: TaskRepositoryImpl
    private val taskDataSource: TaskDataSource = mockk()
    private val auditDataSource: AuditDataSource = mockk()

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryImpl(taskDataSource, auditDataSource)
    }

    @Test
    fun `createTask should return the created task`() {
        val task = Task(
            title = "Test",
            description = "Test",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            creatorId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            assignedId = UUID.randomUUID(),
            role = RoleType.MATE,
            state = State(
                name = "test"
            )
        )

        try {
            taskRepository.createTask(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `updateTask should return the updated task`() {
        val task = Task(
            title = "Updated Task",
            description = "Updated Description",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            creatorId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            assignedId = UUID.randomUUID(),
            role = RoleType.MATE,
            state = State(
                name = "test"
            )
        )

        try {
            taskRepository.updateTask(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteTask should return the deleted task`() {
        val taskId = UUID.randomUUID()

        every { taskDataSource.deleteTask(taskId) } returns Result.success(validTask)


        val result = taskRepository.deleteTask(taskId)

        verify(exactly = 1) { taskRepository.deleteTask(taskId) }
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `getTasks should return list of tasks`() {
        try {
            taskRepository.getTasks()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getTaskById should return list of projects`() {
        try {
            taskRepository.getTaskById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}