package data.repository

import com.google.common.truth.Truth.assertThat
import utils.TaskMock.validTask
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.task.TaskDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockUser.validUser
import utils.TaskMock.inProgressTask
import java.io.IOException
import java.util.UUID

//todo change those testcases
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
        try {
            taskRepository.createTask(validTask)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `updateTask should return success if the task is updated`() {
        every { taskDataSource.updateTask(inProgressTask) } returns Result.success(inProgressTask)
        justRun { auditDataSource.createAuditLog(any()) }

        val result = taskRepository.updateTask(inProgressTask,validTask,validUser, changedField)

        assertThat(result.getOrNull()).isEqualTo(inProgressTask)
    }

    @Test
    fun `updateTask should return failure if the data source throws an exception`() {
        val exception = IOException("Error updating task")
        every { taskDataSource.updateTask(validTask) } returns Result.failure(exception)

        val result = taskRepository.updateTask(validTask, inProgressTask,validUser, changedField)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
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