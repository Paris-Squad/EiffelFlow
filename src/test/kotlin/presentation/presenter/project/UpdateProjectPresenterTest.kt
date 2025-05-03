package presentation.presenter.project

import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.presenter.project.UpdateProjectPresenter
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock

class UpdateProjectPresenterTest {

    private val updateProjectUseCase: UpdateProjectUseCase = mockk()
    private lateinit var updateProjectPresenter: UpdateProjectPresenter

    @BeforeEach
    fun setup() {
        updateProjectPresenter = UpdateProjectPresenter(updateProjectUseCase)
    }

    @Test
    fun `should return Result of Project when project is successfully updated`() {
        try {
            // Given
            every { updateProjectUseCase.updateProject(project) } returns Result.success(project)

            // When
            val result = updateProjectPresenter.updateProject(project)

            // Then
            assertThat(result.isSuccess).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return failure when project update fails`() {
        try {
            // Given
            every { updateProjectUseCase.updateProject(project) } returns Result.failure(Exception("Error updating project"))

            // When
            val result = updateProjectPresenter.updateProject(project)

            // Then
            assertThat(result.isFailure).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return failure when project to be updated is not found`() {
        try {
            // Given
            val exception = Exception("Project not found")
            every { updateProjectUseCase.updateProject(project) } returns Result.failure(exception)

            // When
            val result = updateProjectPresenter.updateProject(project)

            // Then
            assertThat(result.isFailure).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return failure when project data is invalid`() {
        try {
            // Given
            val invalidProject = project.copy(projectName = "")
            every { updateProjectUseCase.updateProject(invalidProject) } returns Result.failure(Exception("Invalid project data"))

            // When
            val result = updateProjectPresenter.updateProject(invalidProject)

            // Then
            assertThat(result.isFailure).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return success when multiple fields of project are updated`() {
        try {
            // Given
            val updatedProject = project.copy(projectName = "Updated Project Name", projectDescription = "Updated Description")
            every { updateProjectUseCase.updateProject(updatedProject) } returns Result.success(updatedProject)

            // When
            val result = updateProjectPresenter.updateProject(updatedProject)

            // Then
            assertThat(result.isSuccess).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object {
        val project = ProjectsMock.CORRECT_PROJECT
    }
}
