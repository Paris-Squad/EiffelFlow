package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.CreateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validTask
import utils.UserMock
import java.io.IOException


class CreateTaskUseCaseTest {

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private val taskRepository: TaskRepository = mockk()
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)



    @BeforeEach
    fun setUp() {
        createTaskUseCase = CreateTaskUseCase(taskRepository=taskRepository , auditRepository = auditRepository)
    }

    @Test
    fun `should return Created task and create audit log when task is created successfully`() {
        runTest {
            //Given
            every { sessionManger.getUser() } returns UserMock.validUser
            coEvery { taskRepository.createTask(validTask) } returns validTask

            //When
            val result = createTaskUseCase.createTask(validTask)

            assertThat(result).isEqualTo(validTask)
            coVerify(exactly = 1) { taskRepository.createTask(validTask) }
            coVerify(exactly = 1) { auditRepository.createAuditLog(any()) }
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
