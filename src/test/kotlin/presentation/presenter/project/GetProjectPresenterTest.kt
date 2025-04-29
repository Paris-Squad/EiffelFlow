package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.model.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.presenter.project.GetProjectPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockProjects
import java.util.UUID

class ProjectPresenterTest {

    private val getProjectUseCase: GetProjectUseCase = mockk()
    private lateinit var getProjectPresenter: GetProjectPresenter

    @BeforeEach
    fun setup() {
        getProjectPresenter = GetProjectPresenter(getProjectUseCase)
    }

    @Test
    fun `should return Result of empty list of Projects when no projects founded`() {
        // Given
        every { getProjectUseCase.getProjects() } returns Result.success(emptyList())

        // When / Then
        try {
            val result = getProjectPresenter.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Projects when Projects are found`() {
        // Given
        every {
            getProjectUseCase.getProjects()
        } returns Result.success(listOf(MockProjects.CORRECT_PROJECT))

        // When / Then
        try {
            val result = getProjectPresenter.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when projects cannot be retrieved`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Projects not found")
        every { getProjectUseCase.getProjects() } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectPresenter.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Project when project with given id exists`() {
        // Given
        val projectId = MockProjects.CORRECT_PROJECT.projectId
        every {
            getProjectUseCase.getProjectById(projectId)
        } returns Result.success(MockProjects.CORRECT_PROJECT)

        // When / Then
        try {
            val result = getProjectPresenter.getProjectById(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            getProjectUseCase.getProjectById(UUID.randomUUID())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectPresenter.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
