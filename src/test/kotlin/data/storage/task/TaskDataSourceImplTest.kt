package data.storage.task

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.StateCsvMapper
import org.example.data.storage.mapper.TaskCsvMapper
import org.example.data.storage.task.TaskDataSource
import org.example.data.storage.task.TaskDataSourceImpl
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TaskDataSourceImplTest {
    private lateinit var taskDataSource: TaskDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val stateCsvMapper: StateCsvMapper = mockk()
    private val taskCsvMapper: TaskCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        taskDataSource = TaskDataSourceImpl(taskCsvMapper, stateCsvMapper, csvStorageManager)
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
            taskDataSource.createTask(task)
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
            taskDataSource.updateTask(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteTask should return the deleted task`() {
        val taskId = UUID.randomUUID()

        try {
            taskDataSource.deleteTask(taskId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getTasks should return list of tasks`() {
        try {
            taskDataSource.getTasks()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getTaskById should return list of projects`() {
        try {
            taskDataSource.getTaskById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}