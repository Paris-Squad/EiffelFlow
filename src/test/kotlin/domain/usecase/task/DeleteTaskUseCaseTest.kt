package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validTask
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
        every { taskRepository.deleteTask(taskIdToDelete) } returns validTask

        val result = deleteTaskUseCase.deleteTask(taskIdToDelete)

        assertThat(result).isEqualTo(validTask)


    }

    @Test
    fun `deleteTask should return failure when task not found`() {
        val taskIdNotFound = UUID.randomUUID()

        every {
            taskRepository.deleteTask(taskIdNotFound)
        } throws EiffelFlowException.NotFoundException("Task not found: $taskIdNotFound")

        assertThrows<EiffelFlowException.NotFoundException> {
            deleteTaskUseCase.deleteTask(taskIdNotFound)
        }

    }

    @Test
    fun `deleteTask should return failure when there is an error during deletion`() {
        val taskIdWithError = UUID.randomUUID()

        every {
            taskRepository.deleteTask(taskIdWithError)
        } throws EiffelFlowException.IOException(null)


        assertThrows<EiffelFlowException.IOException> {
            deleteTaskUseCase.deleteTask(taskIdWithError)
        }

    }
}

