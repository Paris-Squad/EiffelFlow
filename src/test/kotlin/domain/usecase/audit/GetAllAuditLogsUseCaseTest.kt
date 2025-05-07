package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        runTest {
            // Given
            coEvery {
                auditRepository.getAuditLogs()
            } returns listOf(MockAuditLog.AUDIT_LOG)
            // When
            val result = getAllAuditLogsUseCase.getAllAuditLogs()
            // Then
            assertThat(result).containsExactly(MockAuditLog.AUDIT_LOG)
        }
    }

    @Test
    fun `should return Result of NotFoundException when no logs exist`() {
        runTest {
            // Given
            coEvery {
                auditRepository.getAuditLogs()
            } throws EiffelFlowException.NotFoundException("Audit logs not found")
            // When / Then
            assertThrows<EiffelFlowException.NotFoundException>{
                getAllAuditLogsUseCase.getAllAuditLogs()
            }
        }
    }
}