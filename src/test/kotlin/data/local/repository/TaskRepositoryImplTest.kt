package data.local.repository

import com.google.common.truth.Truth.assertThat

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock
import java.io.IOException
import java.util.UUID
import org.example.data.local.csvrepository.TaskRepositoryImpl
import org.example.data.local.FileDataSource
import org.example.data.local.parser.TaskCsvParser
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.assertThrows


class TaskRepositoryImplTest {

    private val fileDataSource: FileDataSource = mockk(relaxed = true)
    private val taskMapper: TaskCsvParser = mockk(relaxed = true)
    private lateinit var taskRepository: TaskRepositoryImpl

    private val changedField = "title"

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryImpl(
            taskCsvParser = taskMapper,
            fileDataSource = fileDataSource
        )
    }

    //region createTask
    @Test
    fun `createTask should return the created task`() {
        runTest {
            // Given
            every { taskMapper.serialize(TaskMock.validTask) } returns TaskMock.ValidTaskCSV
            every { fileDataSource.writeLinesToFile(TaskMock.ValidTaskCSV) } just runs

            // When
            val result = taskRepository.createTask(TaskMock.validTask)

            // Then
            assertThat(result).isEqualTo(TaskMock.validTask)
        }
    }

    @Test
    fun `createTask should return failure when task creation fails`() {
        runTest {
            //Given
            every { taskMapper.serialize(TaskMock.validTask) } throws IOException("Error")
            every { fileDataSource.writeLinesToFile(TaskMock.ValidTaskCSV) } just runs
            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                taskRepository.createTask(TaskMock.validTask)
            }
            assertThat(exception.message).contains("Can't perform this action")
        }
    }
    //endregion

    //region updateTask
    @Test
    fun `updateTask should return success if the task is updated`() {
        runTest {
            // Given
            every { taskMapper.serialize(TaskMock.validTask) } returns TaskMock.ValidTaskCSV
            every { taskMapper.serialize(TaskMock.inProgressTask) } returns TaskMock.ValidTaskCSV
            every { fileDataSource.updateLinesToFile(TaskMock.ValidTaskCSV, TaskMock.ValidTaskCSV) } just runs

            // When
            val result = taskRepository.updateTask(TaskMock.inProgressTask, TaskMock.validTask, changedField)

            // Then
            assertThat(result).isEqualTo(TaskMock.inProgressTask)
        }
    }

    @Test
    fun `updateTask should throw IOException when an exception occurs during update`() {
        runTest {
            // Given
            val exceptionMessage = "File update failed"

            every { taskMapper.serialize(any()) } throws Exception(exceptionMessage)
            every { fileDataSource.updateLinesToFile(any(), any()) } throws Exception(exceptionMessage)

            // When
            val exception = assertThrows<EiffelFlowException.IOException> {
                taskRepository.updateTask(TaskMock.inProgressTask, TaskMock.validTask, changedField)
            }

            // Then
            assertThat(exception.message).isEqualTo("Can't perform this action because $exceptionMessage")
        }
    }
    //endregion

    //region deleteTask
    @Test
    fun `deleteTask should return the deleted task`() {
        runTest {
            //Given
            val lines = listOf(TaskMock.ValidTaskCSV)
            every { fileDataSource.readLinesFromFile() } returns lines
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

            //When
            val result = taskRepository.deleteTask(TaskMock.validTask.taskId)

            //Then
            assertThat(result).isEqualTo(TaskMock.validTask)
        }
    }

    @Test
    fun `deleteTask should return failure when task not found`() {
        runTest {
            // Given
            val nonExistentTaskId = UUID.randomUUID()
            every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                taskRepository.deleteTask(nonExistentTaskId)
            }
            assertThat(exception.message).contains("Task not found")
        }
    }

    @Test
    fun `deleteTask should return failure when exception is thrown`() {
        runTest {
            // Given
            val taskId = TaskMock.validTask.taskId

            every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
            every { fileDataSource.deleteLineFromFile(any()) } throws IOException("Error deleting task")
            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                taskRepository.deleteTask(taskId)
            }
            assertThat(exception.message).contains("Can't perform this action because")
        }
    }
    //endregion

    //region getTasks
    @Test
    fun `getTasks should return list of tasks`() {
        runTest {
            // Given
            val csvLines = listOf(TaskMock.ValidTaskCSV)
            val taskList = listOf(TaskMock.validTask)

            every { fileDataSource.readLinesFromFile() } returns csvLines
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
            // When
            val result = taskRepository.getTasks()
            // Then
            assertThat(result).isEqualTo(taskList)
        }
    }

    @Test
    fun `getTasks should return failure when no tasks found`() {
        runTest {
            // Given
            every { fileDataSource.readLinesFromFile() } returns emptyList()
            // When / Then
            val exception = assertThrows<EiffelFlowException.NotFoundException> {
                taskRepository.getTasks()
            }
            assertThat(exception.message).contains("No tasks found")
        }
    }

    @Test
    fun `getTasks should throw IOException when an unexpected error occurs while fetching tasks`() {
        runTest {
            // Given
            val exceptionMessage = "An unexpected error occurred"
            every { fileDataSource.readLinesFromFile() } throws Exception(exceptionMessage)

            // When
            val exception = assertThrows<EiffelFlowException.IOException> {
                taskRepository.getTasks()
            }

            // Then
            assertThat(exception.message).isEqualTo("Can't perform this action because $exceptionMessage")
        }
    }
    //endregion

    //region getTaskById
    @Test
    fun `getTaskById should return task when found`() {
        runTest {
            // Given
            every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
            // When
            val result = taskRepository.getTaskById(TaskMock.validTask.taskId)
            // Then
            assertThat(result).isEqualTo(TaskMock.validTask)
        }
    }

    @Test
    fun `getTaskById should return failure when task not found`() {
        runTest {
            // Given
            val nonExistentTaskId = UUID.randomUUID()
            // When
            every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
            every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
            // Then
            val exception = assertThrows<EiffelFlowException.NotFoundException> {
                taskRepository.getTaskById(nonExistentTaskId)
            }

            assertThat(exception.message).contains("Task not found")
        }
    }

    @Test
    fun `getTaskById should throw NotFoundException when task is not found in the file`() {
        runTest {
            // Given
            val taskId = UUID.randomUUID()
            val lines = listOf("task1,csv,line", "task2,csv,line")

            every { fileDataSource.readLinesFromFile() } returns lines
            every { taskMapper.parseCsvLine(any()) } returns TaskMock.validTask.copy(taskId = UUID.randomUUID())

            // When
            val exception = assertThrows<EiffelFlowException.NotFoundException> {
                taskRepository.getTaskById(taskId)
            }

            // Then
            assertThat(exception.message).isEqualTo("Task not found")
        }
    }
    //endregion
}