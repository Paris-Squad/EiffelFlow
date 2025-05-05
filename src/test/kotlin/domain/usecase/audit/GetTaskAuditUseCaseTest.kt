package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        // When
        val result = getTaskAuditUseCase.getTaskAuditLogsById(TaskMock.mockTaskId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(MockAuditLog.AUDIT_LOG)
    }

    @Test
    fun `should return Result of ElementNotFoundException when task with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Task not found")
        every {
            auditRepository.getTaskAuditLogById(any())
        } returns Result.failure(exception)

        // When
        val result = getTaskAuditUseCase.getTaskAuditLogsById(TaskMock.mockTaskId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}