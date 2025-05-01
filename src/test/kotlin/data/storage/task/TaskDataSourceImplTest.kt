package data.storage.task

import com.google.common.truth.Truth.assertThat
import utils.TaskMock.ValidTaskCSV
import utils.TaskMock.validTask
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
import utils.TaskMock.inProgressTask
import java.io.IOException
import java.util.*

class TaskDataSourceImplTest {
    private lateinit var taskDataSource: TaskDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val taskMapper: TaskCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        taskDataSource = TaskDataSourceImpl(taskMapper, csvStorageManager)
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
        every { taskMapper.mapTo(inProgressTask) } returns ValidTaskCSV
        every { csvStorageManager.updateLinesToFile(ValidTaskCSV, ValidTaskCSV) } just runs

        val result = taskDataSource.updateTask(validTask, inProgressTask)

        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `updateTask should return failure when exception is thrown from the csvStorageManager`() {
        val exception = IOException("Error")
        every { taskMapper.mapTo(validTask) } returns ValidTaskCSV
        every { taskMapper.mapTo(inProgressTask) } returns ValidTaskCSV
        every { csvStorageManager.updateLinesToFile(ValidTaskCSV, ValidTaskCSV) } throws exception

        val result = taskDataSource.updateTask(validTask, inProgressTask)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
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
        val csvLines = listOf(ValidTaskCSV)
        val taskList = listOf(validTask)

        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask

        val result = taskDataSource.getTasks()

        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getTasks should return failure when no tasks found`() {
        every { csvStorageManager.readLinesFromFile() } returns  emptyList()

        val result = taskDataSource.getTasks()

        assertThat(result.exceptionOrNull()).isInstanceOf(org.example.domain.model.exception.EiffelFlowException.TaskNotFoundException::class.java)
    }

    @Test
    fun `getTaskById should return task when found`() {
        every { csvStorageManager.readLinesFromFile() } returns listOf(ValidTaskCSV)
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask

        val result = taskDataSource.getTaskById(validTask.taskId)

        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `getTaskById should return failure when task not found`() {
        val nonExistentTaskId = UUID.randomUUID()

        every { csvStorageManager.readLinesFromFile() } returns listOf(ValidTaskCSV)
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask

        val result = taskDataSource.getTaskById(nonExistentTaskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(org.example.domain.model.exception.EiffelFlowException.TaskNotFoundException::class.java)
    }
}