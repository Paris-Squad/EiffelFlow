package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.task.ViewTaskHistoryUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validAuditLog
import java.util.*

class ViewTaskHistoryTest {

    private lateinit var auditRepository: AuditRepository
    private lateinit var viewTaskHistoryUseCase: ViewTaskHistoryUseCase
    private lateinit var taskId: UUID


    @BeforeEach
    fun setUp() {
        auditRepository = mockk()
        viewTaskHistoryUseCase = ViewTaskHistoryUseCase(auditRepository)
        taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    }

    @Test
    fun `viewTaskHistory should return audit logs for a valid task`() {
        // Given
        every { auditRepository.getTaskAuditLogById(taskId) } returns listOf(validAuditLog)

        // When
        val result = viewTaskHistoryUseCase.viewTaskHistory(taskId)

        // Then
        assertThat(result).isEqualTo(listOf(validAuditLog))
    }

    @Test
    fun `viewTaskHistory should fail when task is not found`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("History not found")
        every { auditRepository.getTaskAuditLogById(taskId) } throws exception
        // When / Then
        assertThrows<EiffelFlowException.NotFoundException> {
            viewTaskHistoryUseCase.viewTaskHistory(taskId)
        }
    }

    @Test
    fun `viewTaskHistory should return failure when there is an error during retrieval`() {
        // Given
        every { auditRepository.getTaskAuditLogById(taskId) } throws EiffelFlowException.IOException(null)
        // When / Then
        assertThrows<EiffelFlowException> {
            viewTaskHistoryUseCase.viewTaskHistory(taskId)
        }
    }
}
