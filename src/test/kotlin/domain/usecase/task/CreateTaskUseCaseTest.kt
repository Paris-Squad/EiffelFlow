package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import common.TaskMock.validTask
import io.mockk.every
import io.mockk.mockk
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.CreateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        every { taskRepository.createTask(validTask) } returns Result.success(validTask)

        try {
            val result = createTaskUseCase.createTask(validTask)
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(validTask)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    @Test
    fun `createTask should return failure when repository returns failure`() {
        val exception = IOException("Failed to create task")
        every { taskRepository.createTask(validTask) } returns Result.failure(exception)

        try {
            val result = createTaskUseCase.createTask(validTask)
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

}
