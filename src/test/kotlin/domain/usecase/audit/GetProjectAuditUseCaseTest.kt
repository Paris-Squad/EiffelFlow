package domain.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        // Given
        every {
            auditRepository.getItemAuditLogById(any())
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        // When / Then
        try {
            val result = getProjectAuditUseCase.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            auditRepository.getItemAuditLogById(any())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectAuditUseCase.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

}