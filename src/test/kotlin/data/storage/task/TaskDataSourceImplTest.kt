package data.storage.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.TaskCsvMapper
import org.example.data.storage.task.TaskDataSource
import org.example.data.storage.task.TaskDataSourceImpl
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock.ValidTaskCSV
import utils.TaskMock.inProgressTask
import utils.TaskMock.validTask
import java.io.IOException
import java.util.*

class TaskDataSourceImplTest {
    private lateinit var taskDataSource: TaskDataSource
    private val csvStorageManager: CsvStorageManager = mockk(relaxed = true)
    private val taskMapper: TaskCsvMapper = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        taskDataSource = TaskDataSourceImpl(taskMapper, csvStorageManager)
    }

    @Test
    fun `createTask should return the created task on success`() {
        every { taskMapper.mapTo(validTask) } returns ValidTaskCSV
        every { csvStorageManager.writeLinesToFile(ValidTaskCSV) } just runs

        val result = taskDataSource.createTask(validTask)

        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `createTask should return failure when exception is thrown`() {
        every { taskMapper.mapTo(validTask) } throws IOException("Error")
        every { csvStorageManager.writeLinesToFile(ValidTaskCSV) } just runs

        val result = taskDataSource.createTask(validTask)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskCreationException::class.java)
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

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskCreationException::class.java)
    }

    @Test
    fun `deleteTask should return the deleted task when task exists`() {
        val taskId = validTask.taskId
        val lines = listOf(ValidTaskCSV)

        every { csvStorageManager.readLinesFromFile() } returns lines
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask
        every { csvStorageManager.deleteLineFromFile(ValidTaskCSV) } just runs

        val result = taskDataSource.deleteTask(taskId)

        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `deleteTask should return failure when task not found`() {
        val nonExistentTaskId = UUID.randomUUID()
        val lines = listOf(ValidTaskCSV)

        every { csvStorageManager.readLinesFromFile() } returns lines
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask

        val result = taskDataSource.deleteTask(nonExistentTaskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
    }

    @Test
    fun `deleteTask should return failure when exception is thrown`() {
        val taskId = validTask.taskId
        val exception = IOException("Error deleting task")

        every { csvStorageManager.readLinesFromFile() } returns listOf(ValidTaskCSV)
        every { taskMapper.mapFrom(ValidTaskCSV) } returns validTask
        every { csvStorageManager.deleteLineFromFile(any()) } throws exception

        val result = taskDataSource.deleteTask(taskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskDeletionException::class.java)
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
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        val result = taskDataSource.getTasks()

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
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

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
    }
}