package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockProjects


class UpdateProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk()
    private lateinit var updateProjectUseCase: UpdateProjectUseCase

    @BeforeEach
    fun setup() {
        updateProjectUseCase = UpdateProjectUseCase(projectRepository)
    }

    @Test
    fun `should return Result of updated Project when update is successful`() {
        // Given
        val initialProject = MockProjects.CORRECT_PROJECT.copy(projectName = "Initial Name")
        val updatedProject = initialProject.copy(projectName = "Updated Name")
        every { projectRepository.updateProject(updatedProject) } returns Result.success(updatedProject)

        // When
        val result = updateProjectUseCase.updateProject(updatedProject)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(updatedProject)
    }

    @Test
    fun `should return Result of failure with the same Project when update fails`() {
        // Given
        val projectToUpdate = MockProjects.CORRECT_PROJECT.copy(projectName = "Attempted Update")
        val exception = EiffelFlowException.ProjectUpdateException("Failed to update project")
        every { projectRepository.updateProject(projectToUpdate) } returns Result.failure(exception)

        // When
        val result = updateProjectUseCase.updateProject(projectToUpdate)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `should return Result of ElementNotFoundException when project to update does not exist`() {
        // Given
        val nonExistentProject = MockProjects.CORRECT_PROJECT.copy(projectId = java.util.UUID.randomUUID(), projectName = "Non-existent")
        val exception = EiffelFlowException.ProjectNotFoundException("Project with id ${nonExistentProject.projectId} not found")
        every { projectRepository.updateProject(nonExistentProject) } returns Result.failure(exception)

        // When
        val result = updateProjectUseCase.updateProject(nonExistentProject)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        assertThat((result.exceptionOrNull() as? EiffelFlowException.ProjectNotFoundException)?.message)
            .contains("Project with id ${nonExistentProject.projectId} not found")
    }

    @Test
    fun `should call repository's updateProject function with the provided project`() {
        // Given
        val projectToUpdate = MockProjects.CORRECT_PROJECT.copy(projectName = "Check Repo Call")
        every { projectRepository.updateProject(projectToUpdate) } returns Result.success(projectToUpdate)

        // When
        updateProjectUseCase.updateProject(projectToUpdate)

        // Then
        io.mockk.verify(exactly = 1) { projectRepository.updateProject(projectToUpdate) }
    }
}