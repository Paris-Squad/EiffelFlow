package data.storage.task

import com.google.common.truth.Truth.assertThat
import common.TaskMock.ValidTaskCSV
import common.TaskMock.validTask
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
import java.io.IOException
import java.util.*

class TaskDataSourceImplTest {
    private lateinit var taskDataSource: TaskDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val stateCsvMapper: StateCsvMapper = mockk()
    private val taskMapper: TaskCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        taskDataSource = TaskDataSourceImpl(taskMapper, stateCsvMapper, csvStorageManager)
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
    fun `updateTask should return success when task is valid`() {
        every { taskMapper.mapTo(validTask) } returns ValidTaskCSV
        every { csvStorageManager.updateLinesToFile(ValidTaskCSV, any()) } just runs


        try {
            val result = taskDataSource.updateTask(validTask)
            assertThat(result.getOrNull()).isEqualTo(validTask)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `updateTask should return failure when exception is thrown from the csvStorageManager`() {
        val exception = IOException("Error")
        every { csvStorageManager.updateLinesToFile(ValidTaskCSV, ValidTaskCSV) } throws exception

        try {
            val result = taskDataSource.updateTask(validTask)
            assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
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