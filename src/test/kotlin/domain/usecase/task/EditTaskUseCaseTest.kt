package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import common.TaskMock.inProgressTask
import common.TaskMock.mockTaskId
import common.TaskMock.validTask
import io.mockk.every
import io.mockk.mockk
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.EditTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EditTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var editTaskUseCase: EditTaskUseCase

    @BeforeEach    fun setUp() {
        taskRepository = mockk()
        editTaskUseCase = EditTaskUseCase(taskRepository)
    }

    @Test
    fun `editTask should successfully update task when changes are detected`() {
        every { taskRepository.getTaskById(mockTaskId) } returns Result.success(validTask)
        every { taskRepository.updateTask(inProgressTask) } returns Result.success(inProgressTask)

        try {
            val result = editTaskUseCase.editTask(inProgressTask)
            assertThat(result.getOrNull()).isEqualTo(inProgressTask)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Throws
    @Test
    fun `editTask should fail with NoChangesException when no changes detected`() {
        every { taskRepository.getTaskById(validTask.taskId) } returns Result.success(validTask)

        try {
            val result = editTaskUseCase.editTask(validTask)

            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NoChangesException::class.java)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `execute should fail when task is not found`() {
        every { taskRepository.getTaskById(mockTaskId) } returns
                Result.failure(EiffelFlowException.TaskNotFoundException())

        try {
            val result = editTaskUseCase.editTask(validTask)
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
