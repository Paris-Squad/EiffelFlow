package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.model.RoleType
import org.example.domain.model.TaskState
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.EditTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock.inProgressTask
import utils.TaskMock.validTask
import utils.UserMock.validUser
import java.util.*

class EditTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var editTaskUseCase: EditTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        editTaskUseCase = EditTaskUseCase(taskRepository)
    }

    @Test
    fun `editTask should successfully update task when changes are detected`() {
        every { taskRepository.getTaskById(inProgressTask.taskId) } returns Result.success(validTask)
        every { taskRepository.updateTask(inProgressTask, validTask,  any()) } returns Result.success(
            inProgressTask
        )

        val result = editTaskUseCase.editTask(inProgressTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(inProgressTask)
        assertThat(result.isSuccess).isTrue()
        verify {
            taskRepository.updateTask(
                inProgressTask, validTask,  match { it.contains("STATE") })
        }
    }

    @Test
    fun `editTask should fail with IOException when no changes detected`() {
        val exception = EiffelFlowException.IOException(null)
        every { taskRepository.getTaskById(validTask.taskId) } returns Result.success(validTask)

        val result = editTaskUseCase.editTask(validTask, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `editTask should fail when task is not found`() {
        val exception = EiffelFlowException.NotFoundException("Task not found")
        every { taskRepository.getTaskById(validTask.taskId) } returns Result.failure(exception)

        val result = editTaskUseCase.editTask(validTask, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `editTask should identify title changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(title = "Updated Title")

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("TITLE") }
            )
        }
    }

    @Test
    fun `editTask should identify description changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(description = "Updated description")

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("DESCRIPTION") })
        }
    }

    @Test
    fun `editTask should identify assignee changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(assignedId = UUID.randomUUID())

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("ASSIGNEE") }
            )
        }
    }

    @Test
    fun `editTask should identify role changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(role = RoleType.ADMIN)

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("ROLE") }
            )
        }
    }

    @Test
    fun `editTask should identify project changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(projectId = UUID.randomUUID())

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("PROJECT") }
            )
        }
    }

    @Test
    fun `editTask should identify state changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(state = TaskState(name = "in progress"))

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                
                match { it.contains("STATE") }
            )
        }
    }

    @Throws
    @Test
    fun `editTask  should threw IOException when no fields changed`() {
        val originalTask = validTask
        val updatedTask = validTask.copy() // Same task, no changes

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `editTask should identify when multiple fields are updated`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(
            title = "Updated Title", description = "Updated Description", assignedId = UUID.randomUUID()
        )

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask,  any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask, originalTask,  match {
                    it.contains("TITLE") &&
                            it.contains("DESCRIPTION") &&
                            it.contains("ASSIGNEE")
                })
        }
    }
}