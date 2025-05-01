package data.repository

import com.google.common.truth.Truth.assertThat
import utils.TaskMock.validTask
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock
import utils.TaskMock.inProgressTask
import utils.UserMock.validUser
import java.io.IOException
import java.util.*
import org.example.data.repository.TaskRepositoryImpl

class TaskRepositoryImplTest {

    private val taskDataSource: TaskDataSource = mockk()
    private val auditDataSource: AuditDataSource = mockk()
    private lateinit var taskRepository: TaskRepositoryImpl

    private val changedField = "title"

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryImpl(taskDataSource, auditDataSource)
    }

    @Test
    fun `createTask should return the created task`() {
        // Given
        val task = validTask
        val fakeAuditLog = validAuditLog

        every { taskDataSource.createTask(task) } returns Result.success(task)
        every { auditDataSource.createAuditLog(any()) } returns Result.success(fakeAuditLog)

        // When
        val result = taskRepository.createTask(task)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(task)
    }

    @Test
    fun `createTask should return failure when task creation fails`() {
        val exception = IOException("Task creation failed")

        every { taskDataSource.createTask(validTask) } returns Result.failure(exception)

        try {
            val result = taskRepository.createTask(validTask)

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    @Test
    fun `createTask should return failure when audit creation fails`() {
        val exception = IOException("Audit failed")

        every { taskDataSource.createTask(validTask) } returns Result.success(validTask)
        every { auditDataSource.createAuditLog(any()) } returns Result.failure(exception)

        try {
            val result = taskRepository.createTask(validTask)

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    @Test
    fun `updateTask should return success if the task is updated`() {
        every { taskDataSource.updateTask(inProgressTask, validTask) } returns Result.success(inProgressTask)
        justRun { auditDataSource.createAuditLog(any()) }

        val result = taskRepository.updateTask(inProgressTask, validTask, validUser, changedField)

        assertThat(result.getOrNull()).isEqualTo(inProgressTask)
    }

    @Test
    fun `updateTask should return failure if the data source throws an exception`() {
        val exception = IOException("Error updating task")
        every { taskDataSource.updateTask(validTask, inProgressTask) } returns Result.failure(exception)

        val result = taskRepository.updateTask(validTask, inProgressTask, validUser, changedField)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `deleteTask should return the deleted task`() {
        val taskId = UUID.randomUUID()

        try {
            taskRepository.deleteTask(taskId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getTasks should return list of tasks`() {
        val taskList = listOf(validTask, inProgressTask)
        every { taskDataSource.getTasks() } returns Result.success(taskList)

        val result = taskRepository.getTasks()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getTasks should return failure when datasource fails`() {
        val exception = IOException("Error getting tasks")
        every { taskDataSource.getTasks() } returns Result.failure(exception)

        val result = taskRepository.getTasks()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getTaskById should return task when found`() {
        every { taskDataSource.getTaskById(TaskMock.mockTaskId) } returns Result.success(validTask)

        val result = taskRepository.getTaskById(TaskMock.mockTaskId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `getTaskById should return failure when task not found`() {
        val taskId = UUID.randomUUID()
        val exception = IOException("Task not found")
        every { taskDataSource.getTaskById(taskId) } returns Result.failure(exception)

        val result = taskRepository.getTaskById(taskId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}