package presentation.presenter.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.presenter.audit.GetProjectAuditLogsPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.ProjectsMock

class GetProjectAuditLogPresenterTest {

    private val getProjectAuditUseCase: GetProjectAuditUseCase = mockk()
    private lateinit var getProjectAuditLogsPresenter: GetProjectAuditLogsPresenter

    @BeforeEach
    fun setUp() {
        getProjectAuditLogsPresenter = GetProjectAuditLogsPresenter(getProjectAuditUseCase)
    }

    @Test
    fun `should return Result of list with AuditLogs when project with given id exists`() {
        // Given
        val expectedAuditLogs = listOf(MockAuditLog.AUDIT_LOG)
        coEvery {
            getProjectAuditUseCase.getProjectAuditLogsById(any())
        } returns expectedAuditLogs

        // When
        val result = getProjectAuditLogsPresenter.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)

        // Then
        assertThat(result).isEqualTo(expectedAuditLogs)
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        coEvery {
            getProjectAuditUseCase.getProjectAuditLogsById(any())
        } throws EiffelFlowException.NotFoundException("Project not found")

        // When / Then
        assertThrows<EiffelFlowException.NotFoundException> {
            getProjectAuditLogsPresenter.getProjectAuditLogsById(ProjectsMock.CORRECT_PROJECT.projectId)
        }
    }

}