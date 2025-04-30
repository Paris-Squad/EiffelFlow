package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import domain.usecase.task.MockValidTask.validTask
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
    private lateinit var mockTaskId: UUID


    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
        mockTaskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    }

    @Test
    fun `deleteTask should return success when task exists`() {
        every { taskRepository.deleteTask(mockTaskId) } returns Result.success(validTask)

        try {
            val result = deleteTaskUseCase.deleteTask(mockTaskId)
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(MockValidTask)
            verify(exactly = 1) { taskRepository.deleteTask(mockTaskId) }
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteTask should return failure when task is not found`() {
        every { taskRepository.deleteTask(mockTaskId) } returns Result.failure(EiffelFlowException.TaskNotFoundException())
        try {

            val result = deleteTaskUseCase.deleteTask(mockTaskId)
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
            verify(exactly = 1) { taskRepository.deleteTask(mockTaskId) }
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
