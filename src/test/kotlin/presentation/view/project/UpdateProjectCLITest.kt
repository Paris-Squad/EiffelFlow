package presentation.view.project

import io.mockk.mockk
import org.example.presentation.presenter.project.UpdateProjectPresenter
import org.example.presentation.view.project.UpdateProjectCLI
import org.junit.jupiter.api.BeforeEach
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import utils.ProjectsMock

class UpdateProjectCLITest {

    private val updateProjectPresenter: UpdateProjectPresenter = mockk()
    private lateinit var updateProjectCLI: UpdateProjectCLI

    @BeforeEach
    fun setup() {
        updateProjectCLI = UpdateProjectCLI(updateProjectPresenter)
    }

    @Test
    fun `should print success message when project is updated successfully`() {
        //Given
        every {
            updateProjectPresenter.updateProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        //When
        updateProjectCLI.updateProject(ProjectsMock.CORRECT_PROJECT)

        //Then
        verify(exactly = 1) { updateProjectPresenter.updateProject(ProjectsMock.CORRECT_PROJECT)  }
    }
}
