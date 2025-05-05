/*
package presentation.view.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.presentation.presenter.project.GetProjectPresenter
import org.example.presentation.view.project.GetProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.UUID

class ProjectViewTest {

    private val getProjectPresenter: GetProjectPresenter = mockk()
    private lateinit var getProjectCLI: GetProjectCLI


    @BeforeEach
    fun setup() {
        getProjectCLI = GetProjectCLI(getProjectPresenter)
    }

    @Test
    fun `should print No Project founded when there are no projects`() {
        // Given
        every { getProjectPresenter.getProjects() } returns Result.success(emptyList())

        // When / Then
        try {
            val result = getProjectCLI.displayProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print Projects when Projects founded`() {
        // Given
        every {
            getProjectPresenter.getProjects()
        } returns Result.success(listOf(ProjectsMock.CORRECT_PROJECT))

        // When / Then
        try {
            val result = getProjectCLI.displayProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print error message when no projects founded`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Projects not found")
        every { getProjectPresenter.getProjects() } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectCLI.displayProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    @Test
    fun `should print single Project when project with given id founded`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        every {
            getProjectPresenter.getProjectById(projectId)
        } returns Result.success(ProjectsMock.CORRECT_PROJECT)

        // When / Then
        try {
            val result = getProjectCLI.displayProject(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print error message when project with given id does not founded`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        every {
            getProjectPresenter.getProjectById(UUID.randomUUID())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectCLI.displayProject(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
*/
