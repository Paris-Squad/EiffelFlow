package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.TaskMock

class GetTaskAuditUseCaseTest {

    private val auditRepository: AuditRepository = mockk()
    private lateinit var getTaskAuditUseCase: GetTaskAuditUseCase

    @BeforeEach
    fun setUp() {
        getTaskAuditUseCase = GetTaskAuditUseCase(auditRepository)
    }

    @Test
    fun `should return Result of list with AuditLogs when task with given id exists`() {
        // Given
        every {
            auditRepository.getTaskAuditLogById(any())
        } returns listOf(MockAuditLog.AUDIT_LOG)

        // When
        val result = getTaskAuditUseCase.getTaskAuditLogsById(TaskMock.mockTaskId)

        // Then
        assertThat(result).containsExactly(MockAuditLog.AUDIT_LOG)
    }

    @Test
    fun `should return Result of ElementNotFoundException when task with given id does not exist`() {
        // Given
        every {
            auditRepository.getTaskAuditLogById(any())
        } throws EiffelFlowException.NotFoundException("Task not found")

        // When / Then
        assertThrows<EiffelFlowException.NotFoundException>{
            getTaskAuditUseCase.getTaskAuditLogsById(TaskMock.mockTaskId)
        }

    }
}