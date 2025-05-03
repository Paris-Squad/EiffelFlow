package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog

class GetAllAuditLogsUseCaseTest {

    private val auditRepository: AuditRepository = mockk()
    private lateinit var getAllAuditLogsUseCase: GetAllAuditLogsUseCase

    @BeforeEach
    fun setUp() {
        getAllAuditLogsUseCase = GetAllAuditLogsUseCase(auditRepository)
    }

    @Test
    fun `should return Result of list with all AuditLogs when logs exist`() {
        every {
            auditRepository.getAuditLogs()
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        val result = getAllAuditLogsUseCase.getAllAuditLogs()

        assertThat(result.getOrNull()).containsExactly(MockAuditLog.AUDIT_LOG)
    }

    @Test
    fun `should return Result of NotFoundException when no logs exist`() {
        val exception = EiffelFlowException.NotFoundException("Audit logs not found")
        every {
            auditRepository.getAuditLogs()
        } returns Result.failure(exception)

        val result = getAllAuditLogsUseCase.getAllAuditLogs()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}