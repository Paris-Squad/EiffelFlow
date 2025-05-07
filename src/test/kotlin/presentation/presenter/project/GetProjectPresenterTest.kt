package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.presenter.project.GetProjectPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    fun `should return list of Projects when Projects are found`() {
        runTest {
            // Given
            coEvery {
                getProjectUseCase.getProjects()
            } returns listOf(ProjectsMock.CORRECT_PROJECT)

            // When
            val result = getProjectPresenter.getProjects()

            // Then
            assertThat(result).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)

    @Test
    fun `should throw NotFoundException when no projects founded`() {
        runTest {
            // Given
            coEvery {
                getProjectUseCase.getProjects()
            } throws EiffelFlowException.NotFoundException("Projects not found")

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                getProjectPresenter.getProjects()
            }
        }
    }

    @Test
    fun `should return Project when project with given id exists`() {
        runTest {
            // Given
            val projectId = ProjectsMock.CORRECT_PROJECT.projectId
            coEvery {
                getProjectUseCase.getProjectById(projectId)
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = getProjectPresenter.getProjectById(projectId)

            // Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should throw NotFoundException when project with given id does not exist`() {
        runTest {
            // Given
            val exception = EiffelFlowException.NotFoundException("Project not found")
            coEvery {
                getProjectUseCase.getProjectById(any())
            } throws exception

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                getProjectPresenter.getProjectById(UUID.randomUUID())
            }
        }
    }
}
