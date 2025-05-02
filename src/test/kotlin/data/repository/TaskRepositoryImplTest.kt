package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.runs
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock
import java.io.IOException
import java.util.UUID
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.TaskCsvParser
import org.example.domain.exception.EiffelFlowException

import utils.UserMock

class TaskRepositoryImplTest {

    private val fileDataSource: FileDataSource = mockk(relaxed = true)
    private val taskMapper: TaskCsvParser = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var taskRepository: TaskRepositoryImpl

    private val changedField = "title"

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryImpl(
            taskCsvParser = taskMapper,
            fileDataSource = fileDataSource,
            auditRepository = auditRepository
        )
    }

    //region createTask
    @Test
    fun `createTask should return the created task`() {
        // Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            taskMapper.serialize(TaskMock.validTask)
        } returns TaskMock.ValidTaskCSV
        every {
            fileDataSource.writeLinesToFile(TaskMock.ValidTaskCSV)
        } just runs
        every {
            auditRepository.createAuditLog(any())
        } returns Result.success(TaskMock.validAuditLog)

        // When
        val result = taskRepository.createTask(TaskMock.validTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(TaskMock.validTask)
    }

    @Test
    fun `createTask should return failure when task creation fails`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            taskMapper.serialize(TaskMock.validTask)
        } throws IOException("Error")
        every { fileDataSource.writeLinesToFile(TaskMock.ValidTaskCSV) } just runs

        //When
        val result = taskRepository.createTask(TaskMock.validTask)

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `createTask should return failure when audit creation fails`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            taskMapper.serialize(TaskMock.validTask)
        } returns TaskMock.ValidTaskCSV
        every {
            fileDataSource.writeLinesToFile(TaskMock.ValidTaskCSV)
        } just runs
        every {
            auditRepository.createAuditLog(any())
        } throws IOException("Audit failed")

        //When
        val result = taskRepository.createTask(TaskMock.validTask)

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }
    //endregion

    //region updateTask
    @Test
    fun `updateTask should return success if the task is updated`() {
        //Given
        every { taskMapper.serialize(TaskMock.validTask) } returns TaskMock.ValidTaskCSV
        every { taskMapper.serialize(TaskMock.inProgressTask) } returns TaskMock.ValidTaskCSV
        every {
            fileDataSource.updateLinesToFile(TaskMock.ValidTaskCSV, TaskMock.ValidTaskCSV)
        } just runs

        justRun { auditRepository.createAuditLog(any()) }

        //When
        val result = taskRepository.updateTask(TaskMock.inProgressTask, TaskMock.validTask, changedField)

        //Then
        assertThat(result.getOrNull()).isEqualTo(TaskMock.inProgressTask)
    }

    @Test
    fun `updateTask should return failure when exception is thrown from the fileDataSource`() {
        //Given
        val exception = IOException("Error")
        every { sessionManger.getUser() } returns UserMock.validUser
        every { taskMapper.serialize(TaskMock.validTask) } returns TaskMock.ValidTaskCSV
        every { taskMapper.serialize(TaskMock.inProgressTask) } returns TaskMock.ValidTaskCSV
        every { fileDataSource.updateLinesToFile(TaskMock.ValidTaskCSV, TaskMock.ValidTaskCSV) } throws exception

        //When
        val result = taskRepository.updateTask(TaskMock.validTask, TaskMock.inProgressTask, changedField)

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }
    //endregion

    //region deleteTask
    @Test
    fun `deleteTask should return the deleted task`() {
        //Given
        val lines = listOf(TaskMock.ValidTaskCSV)
        every { fileDataSource.readLinesFromFile() } returns lines
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

        //When
        val result = taskRepository.deleteTask(TaskMock.validTask.taskId)

        //Then
        assertThat(result.getOrNull()).isEqualTo(TaskMock.validTask)
    }

    @Test
    fun `deleteTask should return failure when task not found`() {
        val nonExistentTaskId = UUID.randomUUID()
        val lines = listOf(TaskMock.ValidTaskCSV)

        every { fileDataSource.readLinesFromFile() } returns lines
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

        val result = taskRepository.deleteTask(nonExistentTaskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `deleteTask should return failure when exception is thrown`() {
        val taskId = TaskMock.validTask.taskId
        val exception = IOException("Error deleting task")

        every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask
        every { fileDataSource.deleteLineFromFile(any()) } throws exception

        val result = taskRepository.deleteTask(taskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }
    //endregion

    //region getTasks
    @Test
    fun `getTasks should return list of tasks`() {
        val csvLines = listOf(TaskMock.ValidTaskCSV)
        val taskList = listOf(TaskMock.validTask)

        every { fileDataSource.readLinesFromFile() } returns csvLines
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

        val result = taskRepository.getTasks()

        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getTasks should return failure when no tasks found`() {
        every { fileDataSource.readLinesFromFile() } returns emptyList()

        val result = taskRepository.getTasks()

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }
    //endregion
    
    //region getTaskById
    @Test
    fun `getTaskById should return task when found`() {
        every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

        val result = taskRepository.getTaskById(TaskMock.validTask.taskId)

        assertThat(result.getOrNull()).isEqualTo(TaskMock.validTask)
    }

    @Test
    fun `getTaskById should return failure when task not found`() {
        val nonExistentTaskId = UUID.randomUUID()

        every { fileDataSource.readLinesFromFile() } returns listOf(TaskMock.ValidTaskCSV)
        every { taskMapper.parseCsvLine(TaskMock.ValidTaskCSV) } returns TaskMock.validTask

        val result = taskRepository.getTaskById(nonExistentTaskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }
    //endregion
}