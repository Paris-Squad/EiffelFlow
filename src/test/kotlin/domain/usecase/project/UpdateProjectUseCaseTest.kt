/*
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
import utils.ProjectsMock
import java.util.*

class UpdateProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        updateProjectUseCase = UpdateProjectUseCase(projectRepository)
    }

    @Test
    fun `updateProject should successfully update project when changes are detected`() {
        // Given
        val updatedProject = originalProject.copy(projectDescription = "Updated Description")

        every { projectRepository.getProjectById(originalProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
    }

    @Test
    fun `updateProject should fail with IOException when no changes detected`() {
        // Given
        val exception = EiffelFlowException.IOException(null)
        val updatedProject = originalProject.copy()

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `updateProject should fail when project is not found`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        val updatedProject = originalProject.copy(projectDescription = "Updated Description")

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.failure(exception)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
    }

    @Test
    fun `updateProject should identify projectName changes`() {
        // Given
        val updatedProject = originalProject.copy(projectName = "Updated Project Name")

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
        verify { projectRepository.updateProject(updatedProject,originalProject,match { it.contains("PROJECT_NAME") })}
    }

    @Test
    fun `updateProject should identify projectDescription changes`() {
        // Given
        val updatedProject = originalProject.copy(projectDescription = "Updated Description")

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
        verify { projectRepository.updateProject(updatedProject,originalProject,match { it.contains("PROJECT_DESCRIPTION") }) }
    }

    @Test
    fun `updateProject should identify adminId changes`() {
        // Given
        val updatedProject = originalProject.copy(adminId = UUID.randomUUID())

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
        verify { projectRepository.updateProject(updatedProject, originalProject, match { it.contains("ADMIN_ID") }) }
    }

    @Test
    fun `updateProject should identify taskStates changes`() {
        // Given
        val updatedProject = originalProject.copy(taskStates = listOf(TaskState(name = "Completed")))

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
        verify { projectRepository.updateProject(updatedProject,originalProject,match { it.contains("TASK_STATES") }) }
    }

    @Test
    fun `updateProject should identify multiple fields updated`() {
        // Given
        val updatedProject = originalProject.copy(
            projectName = "Updated Project Name", projectDescription = "Updated Description", adminId = UUID.randomUUID()
        )

        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)
        every { projectRepository.updateProject(updatedProject, originalProject, any()) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
        verify { projectRepository.updateProject(updatedProject, originalProject, match {
                    it.contains("PROJECT_NAME") && it.contains("PROJECT_DESCRIPTION") && it.contains("ADMIN_ID")
                }
            )
        }
    }

    @Test
    fun `updateProject should throw IOException when no fields changed`() {
        // Given
        val updatedProject = originalProject.copy()
        every { projectRepository.getProjectById(updatedProject.projectId) } returns Result.success(originalProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }



    companion object {
        val originalProject = ProjectsMock.CORRECT_PROJECT
    }

}*/
