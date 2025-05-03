package presentation.presenter.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.presenter.audit.GetProjectAuditLogsPresenter
import org.junit.jupiter.api.BeforeEach
import utils.MockAuditLog
import utils.ProjectsMock
import kotlin.test.Test

class GetProjectAuditLogsPresenterTest {

    private val getProjectAuditUseCase: GetProjectAuditUseCase = mockk()
    private lateinit var getProjectAuditPresenter: GetProjectAuditLogsPresenter

    @BeforeEach
    fun setup() {
        getProjectAuditPresenter = GetProjectAuditLogsPresenter(getProjectAuditUseCase)
    }

    @Test
    fun `should return project Audit Logs when the project have Audit Logs`() {
        val logs = listOf(MockAuditLog.AUDIT_LOG, MockAuditLog.AUDIT_LOG)
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns Result.success(logs)

        val result = getProjectAuditPresenter.getProjectAuditLogsById(projectId)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `should return failure when Audit Logs is empty`() {
        val logs = emptyList<MockAuditLog>()
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns Result.failure(
            EiffelFlowException.NotFoundException("No audit records were found for this project")
        )

        val result = getProjectAuditPresenter.getProjectAuditLogsById(projectId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .hasMessageThat()
            .contains("No audit records were found for this project")
    }

    @Test
    fun `should return exception when the project haven't Audit Logs`() {
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns
                Result.failure(Exception("No audit records were found for this project"))

        val result = getProjectAuditPresenter.getProjectAuditLogsById(projectId)

        assertThat(result.isFailure).isTrue()
    }


    companion object {
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
    }

}