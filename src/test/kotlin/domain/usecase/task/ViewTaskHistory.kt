package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.task.ViewTaskHistory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock.validAuditLog
import java.util.*

class ViewTaskHistoryTest {

    private lateinit var auditRepository: AuditRepository
    private lateinit var viewTaskHistoryUseCase: ViewTaskHistory
    private lateinit var taskId: UUID


    @BeforeEach
    fun setUp() {
        auditRepository = mockk()
        viewTaskHistoryUseCase = ViewTaskHistory(auditRepository)
        taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    }

    @Test
    fun `viewTaskHistory should return audit logs for a valid task`() {
        every { auditRepository.getTaskAuditLogById(taskId) } returns Result.success(listOf(validAuditLog))

        val result = viewTaskHistoryUseCase.viewTaskHistory(taskId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(listOf(validAuditLog))
    }

    @Test
    fun `viewTaskHistory should fail when task is not found`() {

        val exception = EiffelFlowException.NotFoundException("History not found")
        every { auditRepository.getTaskAuditLogById(taskId) } returns Result.failure(exception)

        val result =viewTaskHistoryUseCase.viewTaskHistory(taskId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException::class.java)


    }

    @Test
    fun `viewTaskHistory should return failure when there is an error during retrieval`() {
        val exception = EiffelFlowException.IOException(null)
        every { auditRepository.getTaskAuditLogById(taskId) } returns Result.failure(exception)

        val result = viewTaskHistoryUseCase.viewTaskHistory(taskId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }
}
