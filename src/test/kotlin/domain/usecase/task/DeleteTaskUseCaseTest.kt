package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import domain.usecase.task.TaskMock.validTask
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
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

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validTask)
    }

    @Test
    fun `deleteTask should return failure when task not found`() {
        val taskIdNotFound = UUID.randomUUID()

        every { taskRepository.deleteTask(taskIdNotFound) } returns Result.failure(EiffelFlowException.TaskNotFoundException())

        val result = deleteTaskUseCase.deleteTask(taskIdNotFound)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.TaskNotFoundException::class.java)
    }

    @Test
    fun `deleteTask should return failure when there is an error during deletion`() {
        val taskIdWithError = UUID.randomUUID()

        every { taskRepository.deleteTask(taskIdWithError) } returns Result.failure(EiffelFlowException.TaskDeletionException())

        val result = deleteTaskUseCase.deleteTask(taskIdWithError)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(Exception::class.java)
    }
}