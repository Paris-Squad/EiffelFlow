package presentation.presenter.project

import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.presenter.project.UpdateProjectPresenter
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock

class UpdateProjectPresenterTest {

    private val updateProjectUseCase: UpdateProjectUseCase = mockk()
    private lateinit var updateProjectPresenter: UpdateProjectPresenter

    @BeforeEach
    fun setup() {
        updateProjectPresenter = UpdateProjectPresenter(updateProjectUseCase)
    }

    @Test
    fun `should return the updated Project when project is successfully updated`() {

        // Given
        every {
            updateProjectUseCase.updateProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        // When
        val result = updateProjectPresenter.updateProject(ProjectsMock.CORRECT_PROJECT)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Test
    fun `should throw IOException when project update fails`() {
        // Given
        every {
            updateProjectUseCase.updateProject(ProjectsMock.CORRECT_PROJECT)
        } throws EiffelFlowException.IOException("Error updating project")

        //When / Then
        assertThrows<EiffelFlowException.IOException> {
            updateProjectPresenter.updateProject(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Test
    fun `should throw NotFoundException when project to be updated is not found`() {
        // Given
        every {
            updateProjectUseCase.updateProject(ProjectsMock.CORRECT_PROJECT)
        } throws EiffelFlowException.NotFoundException("Project not found")

        //When / Then
        assertThrows<EiffelFlowException.NotFoundException> {
            updateProjectPresenter.updateProject(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Test
    fun `should throw IOException when project data is invalid`() {
        // Given
        val invalidProject = ProjectsMock.CORRECT_PROJECT.copy(projectName = "")
        every {
            updateProjectUseCase.updateProject(invalidProject)
        } throws EiffelFlowException.IOException("Invalid project data")

        //When / Then
        assertThrows<EiffelFlowException.IOException> {
            updateProjectPresenter.updateProject(invalidProject)
        }
    }

    @Test
    fun `should return updated project when multiple fields of project are updated`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(
            projectName = "Updated Project Name",
            projectDescription = "Updated Description"
        )
        every { updateProjectUseCase.updateProject(updatedProject) } returns updatedProject

        // When
        val result = updateProjectPresenter.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
    }
}
