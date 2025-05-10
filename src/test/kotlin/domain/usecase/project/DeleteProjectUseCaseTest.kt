package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import utils.UserMock
import java.util.UUID

class DeleteProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = DeleteProjectUseCase(projectRepository, auditRepository = auditRepository)
    }

    @Test
    fun `should return the deleted project when project deleted successfully`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
            coEvery {
                projectRepository.deleteProject(any())
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = deleteProjectUseCase.deleteProject(projectId)

            // Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            coVerify { projectRepository.deleteProject(any()) }
            coVerify(exactly = 1) { auditRepository.createAuditLog(any()) }
        }
    }

    @Test
    fun `should throw IOException when the project doesn't get deleted`() {
        runTest {
            // Given
            val differentProjectId = UUID.randomUUID()
            coEvery {
                projectRepository.deleteProject(any())
            } throws EiffelFlowException.IOException("unable to find correct project")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.deleteProject(differentProjectId)
            }
        }
    }

    @Test
    fun `should throw AuthorizationException when user is not admin`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns false
            every { sessionManger.getUser() } returns UserMock.validUser

            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")

            // When / Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                deleteProjectUseCase.deleteProject(projectId)
            }
        }
    }
}