package presentation.presenter.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.presenter.audit.GetProjectAuditLogsPresenter
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import utils.MockAuditLog
import utils.ProjectsMock
import java.util.*

class GetProjectAuditLogsPresenterTest {

    private val getProjectAuditUseCase: GetProjectAuditUseCase = mockk()
    private lateinit var presenter: GetProjectAuditLogsPresenter

    @BeforeEach
    fun setup() {
        presenter = GetProjectAuditLogsPresenter(getProjectAuditUseCase)
    }

    @Test
    fun `should return audit logs when logs are present`() {
        // Given
        val logs = listOf(MockAuditLog.AUDIT_LOG, MockAuditLog.AUDIT_LOG)
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns Result.success(logs)

        // When
        val result = presenter.getProjectAuditLogsById(projectId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(logs)
    }

    @Test
    fun `should return NotFoundException when audit logs are empty`() {
        // Given
        val emptyLogs = emptyList<AuditLog>()
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns Result.success(emptyLogs)

        // When
        val result = presenter.getProjectAuditLogsById(projectId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("No audit records were found for this project")
    }

    @Test
    fun `should return failure when use case throws generic exception`() {
        // Given
        every { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns
                Result.failure(Exception("Something went wrong"))

        // When
        val result = presenter.getProjectAuditLogsById(projectId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(Exception::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("Something went wrong")
    }

    companion object {
        val projectId: UUID = ProjectsMock.CORRECT_PROJECT.projectId
    }
}
