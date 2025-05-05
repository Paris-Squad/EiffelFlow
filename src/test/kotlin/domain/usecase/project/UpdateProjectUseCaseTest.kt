package domain.usecase.project

import io.mockk.mockk
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import org.example.domain.model.TaskState
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.UUID

class UpdateProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        updateProjectUseCase = UpdateProjectUseCase(projectRepository)
    }

    @Test
    fun `updateProject should successfully update project and return it when changes are detected`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
        every {
            projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
    }

    @Test
    fun `updateProject should throw IOException when no changes detected`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy()

        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT

        // When / Then
        assertThrows<EiffelFlowException.IOException> {
            updateProjectUseCase.updateProject(updatedProject)
        }
    }

    @Test
    fun `updateProject should throw NotFoundException when project is not found`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } throws exception

        // When / Then
        assertThrows<EiffelFlowException.NotFoundException> {
            updateProjectUseCase.updateProject(updatedProject)
        }
    }

    @Test
    fun `updateProject should identify projectName changes`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectName = "Updated Project Name")

        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
        verify {
            projectRepository.updateProject(
                updatedProject, ProjectsMock.CORRECT_PROJECT, match { it.contains("PROJECT_NAME") })
        }
    }

    @Test
    fun `updateProject should identify projectDescription changes`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "Updated Description")
        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns
                updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
        verify {
            projectRepository.updateProject(
                project = updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = match { it.contains("PROJECT_DESCRIPTION") }
            )
        }
    }

    @Test
    fun `updateProject should identify adminId changes`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(adminId = UUID.randomUUID())

        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns
                updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
        verify {
            projectRepository.updateProject(
                project = updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = match { it.contains("ADMIN_ID") }
            )
        }
    }

    @Test
    fun `updateProject should identify taskStates changes`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(taskStates = listOf(TaskState(name = "Completed")))

        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
        verify {
            projectRepository.updateProject(
                project = updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = match { it.contains("TASK_STATES") }
            )
        }
    }

    @Test
    fun `updateProject should identify multiple fields updated`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(
            projectName = "Updated Project Name",
            projectDescription = "Updated Description",
            adminId = UUID.randomUUID()
        )

        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            projectRepository.updateProject(updatedProject, ProjectsMock.CORRECT_PROJECT, any())
        } returns
                updatedProject

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
        verify {
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

    @Test
    fun `updateProject should throw IOException when no fields changed`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy()
        every {
            projectRepository.getProjectById(updatedProject.projectId)
        } returns ProjectsMock.CORRECT_PROJECT

        // When / Then
        assertThrows<EiffelFlowException.IOException> {
            updateProjectUseCase.updateProject(updatedProject)
        }
    }
}