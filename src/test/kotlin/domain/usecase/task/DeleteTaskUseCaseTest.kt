package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock
import utils.UserMock
import java.util.UUID

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var taskIdToDelete: UUID
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)



    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository = taskRepository, auditRepository = auditRepository)
        taskIdToDelete = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    }

    @Test
    fun `deleteTask should return success when task exists`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            coEvery { taskRepository.deleteTask(taskIdToDelete) } returns TaskMock.validTask

            val result = deleteTaskUseCase.deleteTask(taskIdToDelete)

            assertThat(result).isEqualTo(TaskMock.validTask)
        }
    }

    @Test
    fun `deleteTask should return failure when there is an error during deletion`() {
        runTest {
            val taskIdWithError = UUID.randomUUID()

            coEvery {
                taskRepository.deleteTask(taskIdWithError)
            } throws EiffelFlowException.IOException("Deletion error")

            val exception = assertThrows<EiffelFlowException.IOException> {
                deleteTaskUseCase.deleteTask(taskIdWithError) // must throw
            }

            assertThat(exception).isInstanceOf(EiffelFlowException.IOException::class.java)
        }
    }
}