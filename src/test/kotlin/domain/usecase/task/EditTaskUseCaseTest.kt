package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import utils.TaskMock.inProgressTask
import utils.TaskMock.validTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.common.Constants
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.EditTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock.validUser
import java.util.UUID

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
        every { taskRepository.updateTask(inProgressTask, validTask, validUser, any()) } returns Result.success(
            inProgressTask
        )

        val result = editTaskUseCase.editTask(inProgressTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(inProgressTask)
        assertThat(result.isSuccess).isTrue()
        verify {
            taskRepository.updateTask(
                inProgressTask, validTask, validUser, match { it.contains(Constants.TaskField.STATE.displayName) })
        }
    }

    @Test
    fun `editTask should fail with NoChangesException when no changes detected`() {
        val exception = EiffelFlowException.NoChangesException()
        every { taskRepository.getTaskById(validTask.taskId) } returns Result.success(validTask)

        val result = editTaskUseCase.editTask(validTask, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `editTask should fail when task is not found`() {
        val exception = EiffelFlowException.TaskNotFoundException()
        every { taskRepository.getTaskById(validTask.taskId) } returns Result.failure(exception)

        val result = editTaskUseCase.editTask(validTask, validUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `editTask should identify title changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(title = "Updated Title")

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.TITLE.displayName) }
            )
        }
    }

    @Test
    fun `editTask should identify description changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(description = "Updated description")

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.DESCRIPTION.displayName) })
        }
    }

    @Test
    fun `editTask should identify assignee changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(assignedId = UUID.randomUUID())

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.ASSIGNEE.displayName) }
            )
        }
    }

    @Test
    fun `editTask should identify role changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(role = RoleType.ADMIN)

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.ROLE.displayName) }
            )
        }
    }

    @Test
    fun `editTask should identify project changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(projectId = UUID.randomUUID())

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.PROJECT.displayName) }
            )
        }
    }

    @Test
    fun `editTask should identify state changes`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(state = State(name = "in progress"))

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask,
                originalTask,
                validUser,
                match { it.contains(Constants.TaskField.STATE.displayName) }
            )
        }
    }

    @Test
    fun `editTask  should identify when multiple fields are updated`() {
        val originalTask = validTask
        val updatedTask = originalTask.copy(
            title = "Updated Title", description = "Updated Description", assignedId = UUID.randomUUID()
        )

        every { taskRepository.getTaskById(updatedTask.taskId) } returns Result.success(originalTask)
        every { taskRepository.updateTask(updatedTask, originalTask, validUser, any()) } returns Result.success(
            updatedTask
        )

        val result = editTaskUseCase.editTask(updatedTask, validUser)

        assertThat(result.getOrNull()).isEqualTo(updatedTask)

        verify {
            taskRepository.updateTask(
                updatedTask, originalTask, validUser, match {
                    it.contains(Constants.TaskField.TITLE.displayName) &&
                            it.contains(Constants.TaskField.DESCRIPTION.displayName) &&
                            it.contains(Constants.TaskField.ASSIGNEE.displayName)
                })
        }
    }
}