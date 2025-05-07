package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.CreateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validTask
import java.io.IOException


class CreateTaskUseCaseTest {

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private val taskRepository: TaskRepository = mockk()

    @BeforeEach
    fun setUp() {
        createTaskUseCase = CreateTaskUseCase(taskRepository)
    }

    @Test
    fun `createTask should return success when repository returns success`() {
        runTest {
            coEvery { taskRepository.createTask(validTask) } returns validTask

            val result = createTaskUseCase.createTask(validTask)

            assertThat(result).isEqualTo(validTask)
        }
    }


    @Test
    fun `createTask should return failure when repository returns failure`() {
        runTest {
            val exception = IOException("Failed to create task")
            coEvery { taskRepository.createTask(validTask) } throws EiffelFlowException.IOException("Can't create task. ${exception.message}")

            val thrownException = assertThrows<EiffelFlowException.IOException> {
                createTaskUseCase.createTask(validTask)
            }

            assertThat(thrownException.message).contains("Can't create task")
        }
    }
}
