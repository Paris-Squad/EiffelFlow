package presentation.presenter.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.presenter.audit.GetProjectAuditLogsPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
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
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } returns logs

        // When
        val result = presenter.getProjectAuditLogsById(projectId)

        // Then
        assertThat(result).isEqualTo(logs)
    }

    @Test
    fun `should return failure when use case throws generic exception`() {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(projectId) } throws
                Exception("Something went wrong")

        // When / Then
        assertThrows <Exception>{
            presenter.getProjectAuditLogsById(projectId)
        }
    }

    companion object {
        val projectId: UUID = ProjectsMock.CORRECT_PROJECT.projectId
    }
}
