package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.CreateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validTask


class CreateTaskUseCaseTest {

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private val taskRepository: TaskRepository = mockk()

    @BeforeEach
    fun setUp() {
        createTaskUseCase = CreateTaskUseCase(taskRepository)
    }


    @Test
    fun `createTask should return success when repository returns success`() {
        every { taskRepository.createTask(validTask) } returns validTask

        val result = createTaskUseCase.createTask(validTask)

        assertThat(result).isEqualTo(validTask)
    }


    @Test
    fun `createTask should return failure when repository returns failure`() {
        val exception = EiffelFlowException.IOException("Failed to create task")
        every { taskRepository.createTask(validTask) } throws exception


        assertThrows<EiffelFlowException.IOException> {
            createTaskUseCase.createTask(validTask)
        }


    }

}
