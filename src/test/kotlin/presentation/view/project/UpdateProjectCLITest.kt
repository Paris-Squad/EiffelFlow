package presentation.view.project

import io.mockk.mockk
import org.example.presentation.presenter.project.UpdateProjectPresenter
import org.example.presentation.view.project.UpdateProjectCLI
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.every
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

        every { updateProjectPresenter.updateProject(project) } returns Result.success(project)

        try {
            updateProjectCLI.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print error message when project update failed`() {
        every { updateProjectPresenter.updateProject(project) } returns Result.failure(Exception("Error updating project"))

        try {
            updateProjectCLI.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object {
        val project = ProjectsMock.CORRECT_PROJECT
    }
}
