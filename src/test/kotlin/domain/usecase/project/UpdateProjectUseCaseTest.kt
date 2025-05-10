package domain.usecase.project

import io.mockk.mockk
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.model.TaskState
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import utils.UserMock
import java.util.UUID

class UpdateProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)


    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        updateProjectUseCase = UpdateProjectUseCase(projectRepository, auditRepository = auditRepository)
    }

    @Test
    fun `updateProject should successfully update project and return it when changes are detected`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
            coEvery {
                projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify(exactly = 1) { auditRepository.createAuditLog(any()) }
        }
    }

    @Test
    fun `updateProject should throw IOException when no changes detected`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy()

            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                updateProjectUseCase.updateProject(updatedProject)
            }
        }
    }

    @Test
    fun `updateProject should throw NotFoundException when project is not found`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val exception = EiffelFlowException.NotFoundException("Project not found")
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } throws exception

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                updateProjectUseCase.updateProject(updatedProject)
            }
        }
    }

    @Test
    fun `updateProject should identify projectName changes`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectName = "Updated Project Name")

            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify {
                projectRepository.updateProject(
                    updatedProject, ProjectsMock.CORRECT_PROJECT, match { it.contains("PROJECT_NAME") })
            }
        }
    }

    @Test
    fun `updateProject should identify projectDescription changes`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns
                    updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify {
                projectRepository.updateProject(
                    project = updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = match { it.contains("PROJECT_DESCRIPTION") }
                )
            }
        }
    }

    @Test
    fun `updateProject should identify adminId changes`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(adminId = UUID.randomUUID())

            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns
                    updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify {
                projectRepository.updateProject(
                    project = updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = match { it.contains("ADMIN_ID") }
                )
            }
        }
    }

    @Test
    fun `updateProject should identify taskStates changes`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(taskStates = listOf(TaskState(name = "Completed")))

            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify {
                projectRepository.updateProject(
                    project = updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = match { it.contains("TASK_STATES") }
                )
            }
        }
    }

    @Test
    fun `updateProject should identify multiple fields updated`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(
                projectName = "Updated Project Name",
                projectDescription = "Updated Description",
                adminId = UUID.randomUUID()
            )

            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT
            coEvery {
                projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
            } returns
                    updatedProject

            // When
            val result = updateProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result).isEqualTo(updatedProject)
            coVerify {
                projectRepository.updateProject(
                    project = updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = match {
                        it.contains("PROJECT_NAME")
                                && it.contains("PROJECT_DESCRIPTION")
                                && it.contains("ADMIN_ID")
                    })
            }
        }
    }

    @Test
    fun `updateProject should throw IOException when no fields changed`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns true
            every { sessionManger.getUser() } returns UserMock.adminUser
            val updatedProject = ProjectsMock.CORRECT_PROJECT.copy()
            coEvery {
                projectRepository.getProjectById(updatedProject.projectId)
            } returns ProjectsMock.CORRECT_PROJECT

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                updateProjectUseCase.updateProject(updatedProject)
            }
        }
    }

    @Test
    fun `updateProject should throw AuthorizationException when user is not admin`() {
        runTest {
            // Given
            every { sessionManger.isAdmin() } returns false
            every { sessionManger.getUser() } returns UserMock.validUser

            // When / Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                updateProjectUseCase.updateProject(ProjectsMock.CORRECT_PROJECT.copy())  }

        }
    }

}