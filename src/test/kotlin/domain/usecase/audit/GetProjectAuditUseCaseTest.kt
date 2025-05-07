package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.ProjectsMock

class GetProjectAuditUseCaseTest {

    private val auditRepository: AuditRepository = mockk()
    private lateinit var getProjectAuditUseCase: GetProjectAuditUseCase

    @BeforeEach
    fun setUp() {
        getProjectAuditUseCase = GetProjectAuditUseCase(auditRepository)
    }

    @Test
    fun `should return Result of list with AuditLogs when project with given id exists`() {
        runTest {
            // Given
            coEvery {
                auditRepository.getProjectAuditLogById(any())
            } returns listOf(MockAuditLog.AUDIT_LOG)

            // When
            val result = getProjectAuditUseCase.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)

            // Then
            assertThat(result).isEqualTo(listOf(MockAuditLog.AUDIT_LOG))
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        runTest {
            // Given
            val exception = EiffelFlowException.NotFoundException("Project not found")
            coEvery {
                auditRepository.getProjectAuditLogById(any())
            } throws exception

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                getProjectAuditUseCase.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
            }
        }
    }

}
