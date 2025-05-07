package presentation.presenter.project

import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.presenter.project.CreateProjectPresenter
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock

class CreateProjectPresenterTest {

    private val createProjectUseCase: CreateProjectUseCase = mockk()
    private lateinit var createProjectPresenter: CreateProjectPresenter

    @BeforeEach
    fun setup() {
        createProjectPresenter = CreateProjectPresenter(createProjectUseCase)
    }

    @Test
    fun `should return the created Project when project is successfully created`() {
            //Given
            coEvery {
                createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
            } returns ProjectsMock.CORRECT_PROJECT

            //When
            val result = createProjectPresenter.createProject(ProjectsMock.CORRECT_PROJECT)

            //Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)

    }

    @Test
    fun `should throw IOException when project creation fails `() {
            //Given
            coEvery {
                createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
            } throws EiffelFlowException.IOException("Error writing to file")

            //When / Then
            assertThrows<EiffelFlowException.IOException> {
                createProjectPresenter.createProject(ProjectsMock.CORRECT_PROJECT)
            }
    }

    @Test
    fun `should throw Exception when unexpected exception occurs`() {
        // Given
        coEvery { createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT) } throws IllegalStateException("Unexpected failure")

        // When & Then
        val exception = assertThrows<RuntimeException> {
            createProjectPresenter.createProject(ProjectsMock.CORRECT_PROJECT)
        }
        assertThat(exception.message).isEqualTo("An error occurred while creating the project: Unexpected failure")
    }

}