package presentation.view.project

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.presentation.presenter.project.GetProjectPresenter
import org.example.presentation.view.project.GetProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock

class GetProjectCLITest {

    private val getProjectPresenter: GetProjectPresenter = mockk()
    private lateinit var getProjectCLI: GetProjectCLI


    @BeforeEach
    fun setup() {
        getProjectCLI = GetProjectCLI(getProjectPresenter)
    }

    @Test
    fun `should print No Project founded when there are no projects`() {
        // Given
        every { getProjectPresenter.getProjects() } returns emptyList()

        // When
        getProjectCLI.displayProjects()

        // Then
        verify(exactly = 1) { getProjectPresenter.getProjects() }
    }

    @Test
    fun `should print Projects when Projects founded`() {
        // Given
        every {
            getProjectPresenter.getProjects()
        } returns listOf(ProjectsMock.CORRECT_PROJECT)

        // When
        getProjectCLI.displayProjects()

        // Then
        verify(exactly = 1) { getProjectPresenter.getProjects() }
    }

    @Test
    fun `should print single Project when project with given id founded`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        every {
            getProjectPresenter.getProjectById(projectId)
        } returns ProjectsMock.CORRECT_PROJECT

        // When
        getProjectCLI.displayProject(projectId)

        // Then
        verify(exactly = 1) { getProjectPresenter.getProjectById(projectId) }
    }
}
