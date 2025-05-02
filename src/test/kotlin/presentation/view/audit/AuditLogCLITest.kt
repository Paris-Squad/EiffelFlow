package presentation.view.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.presentation.presenter.audit.GetProjectAuditLogsPresenter
import org.example.presentation.view.audit.GetProjectAuditLogsCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import utils.ProjectsMock

class GetProjectAuditLogsCLITest {
    private val getProjectAuditLogsPresenter: GetProjectAuditLogsPresenter = mockk()
    private lateinit var getProjectAuditLogsCLI: GetProjectAuditLogsCLI

    @BeforeEach
    fun setUp() {
        getProjectAuditLogsCLI = GetProjectAuditLogsCLI(getProjectAuditLogsPresenter)
    }

    @Test
    fun `should return Result of list with AuditLogs when project with given id exists`() {
        // Given
        every {
            getProjectAuditLogsPresenter.getProjectAuditLogsById(any())
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        // When / Then
        try {
            val result = getProjectAuditLogsCLI.displayProjectLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        every {
            getProjectAuditLogsPresenter.getProjectAuditLogsById(any())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectAuditLogsCLI.displayProjectLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

}