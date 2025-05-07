package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.presentation.presenter.project.DeleteProjectPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.UUID

class DeleteProjectPresenterTest {

    private val deleteProjectUseCase: DeleteProjectUseCase = mockk()
    private lateinit var deleteProjectPresenter: DeleteProjectPresenter

    @BeforeEach
    fun setUp() {
        deleteProjectPresenter = DeleteProjectPresenter(deleteProjectUseCase)
    }

    @Test
    fun `should return the deleted project when the deleteProject return success`() {
        runTest {
            // Given
            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
            coEvery {
                deleteProjectUseCase.deleteProject(any())
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = deleteProjectPresenter.deleteProject(projectId)

            // Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            coVerify(exactly = 1) { deleteProjectUseCase.deleteProject(any()) }
        }
    }

    @Test
    fun `should throw IOException when deleteProject returns failure`() {
        runTest {
            // Given
            val differentProjectId = UUID.randomUUID()
            coEvery {
                deleteProjectUseCase.deleteProject(any())
            } throws EiffelFlowException.IOException("unable to find correct project")

            //When / Then
            assertThrows<EiffelFlowException.IOException> {
                deleteProjectPresenter.deleteProject(differentProjectId)
            }
        }
    }
}