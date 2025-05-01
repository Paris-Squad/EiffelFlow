package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import utils.TaskMock.validTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var taskIdToDelete: UUID


    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
        taskIdToDelete = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    }

    @Test
    fun `deleteTask should return success when task exists`() {
        every { taskRepository.deleteTask(taskIdToDelete) } returns Result.success(validTask)

        val result = deleteTaskUseCase.deleteTask(taskIdToDelete)

        verify(exactly = 1) { taskRepository.deleteTask(taskIdToDelete) }

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validTask)



    }

    @Test
    fun `deleteTask should return failure when task is not found`() {
        every { taskRepository.deleteTask(taskIdToDelete) } returns Result.failure(EiffelFlowException.TaskNotFoundException())

        val result = deleteTaskUseCase.deleteTask(taskIdToDelete)

        verify(exactly = 1) { taskRepository.deleteTask(taskIdToDelete) }

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)

    }
}
