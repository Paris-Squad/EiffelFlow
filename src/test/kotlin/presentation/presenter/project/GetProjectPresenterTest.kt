package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.presenter.project.GetProjectPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.UUID

class GetProjectPresenterTest {

    private val getProjectUseCase: GetProjectUseCase = mockk(relaxed = true)
    private lateinit var getProjectPresenter: GetProjectPresenter

    @BeforeEach
    fun setup() {
        getProjectPresenter = GetProjectPresenter(getProjectUseCase)
    }

    @Test
    fun `should return Result of Projects when Projects are found`() {
        // Given
        every {
            getProjectUseCase.getProjects()
        } returns Result.success(listOf(ProjectsMock.CORRECT_PROJECT))

        // When
        val result = getProjectPresenter.getProjects()

        // Then
        assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockProjects.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)

    @Test
    fun `should return Result of ElementNotFoundException when projects cannot be retrieved`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Projects not found")
        every { getProjectUseCase.getProjects() } returns Result.failure(exception)

        // When
        val result = getProjectPresenter.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `should return Result of Project when project with given id exists`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        every {
            getProjectUseCase.getProjectById(projectId)
        } returns Result.success(ProjectsMock.CORRECT_PROJECT)

        // When
        val result = getProjectPresenter.getProjectById(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(MockProjects.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            getProjectUseCase.getProjectById(any())
        } returns Result.failure(exception)

        // When
        val result = getProjectPresenter.getProjectById(UUID.randomUUID())

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
