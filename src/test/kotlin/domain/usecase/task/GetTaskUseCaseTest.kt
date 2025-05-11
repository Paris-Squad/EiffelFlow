package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.GetTaskUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock
import java.util.UUID

class GetTaskUseCaseTest {
    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private lateinit var useCase: GetTaskUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetTaskUseCase(taskRepository)
    }

    @Test
    fun `getTasks should return list of tasks`() = runBlocking {
        val tasks = listOf(
            TaskMock.validTask
        )
        coEvery { taskRepository.getTasks() } returns tasks

        val result = useCase.getTasks()

        assertThat(result).isEqualTo(tasks)
        coVerify(exactly = 1) { taskRepository.getTasks() }
    }

    @Test
    fun `getTaskByID should return task by ID`() = runBlocking {
        val taskId = UUID.randomUUID()
        val task = TaskMock.validTask
        coEvery { taskRepository.getTaskById(taskId) } returns task

        val result = useCase.getTaskByID(taskId)

        assertThat(result).isEqualTo(task)
        coVerify(exactly = 1) { taskRepository.getTaskById(taskId) }
    }
}