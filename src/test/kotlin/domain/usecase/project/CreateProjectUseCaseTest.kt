package domain.usecase.project

import io.mockk.mockk
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.CreateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import utils.UserMock

class CreateProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        createProjectUseCase = CreateProjectUseCase(repository = projectRepository, auditRepository = auditRepository)
    }

    @Test
    fun `should return Created Project and create audit log when project is created successfully`() {
        runTest {
            //Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser

            coEvery {
                projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
            } returns ProjectsMock.CORRECT_PROJECT

            //When
            val result = createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)

            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            coVerify(exactly = 1) { projectRepository.createProject(ProjectsMock.CORRECT_PROJECT) }
            coVerify(exactly = 1) { auditRepository.createAuditLog(any()) }
        }
    }

    @Test
    fun `should throw IOException when creating project fails`() {
        runTest {
            //Given
            every { sessionManger.getUser() } returns UserMock.adminUser
            every { sessionManger.isAdmin() } returns true

            coEvery {
                projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
            } throws EiffelFlowException.IOException("Failed to create project")

            //When / Then
            assertThrows<EiffelFlowException.IOException> {
                createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
            }
        }
    }

    @Test
    fun `should throw AuthorizationException when user is not admin`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns false

            // When / Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
            }

            coVerify(exactly = 0) { projectRepository.createProject(any()) }
            coVerify(exactly = 0) { auditRepository.createAuditLog(any()) }
        }
    }
}