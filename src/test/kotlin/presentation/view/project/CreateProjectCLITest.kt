package presentation.view.project

import io.mockk.mockk
import org.example.presentation.presenter.project.CreateProjectPresenter
import org.example.presentation.view.project.CreateProjectCLI
import org.junit.jupiter.api.BeforeEach
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import utils.ProjectsMock

class CreateProjectCLITest {

    private val createProjectPresenter: CreateProjectPresenter = mockk()
    private lateinit var createProjectCLI: CreateProjectCLI

    @BeforeEach
    fun setup() {
        createProjectCLI = CreateProjectCLI(createProjectPresenter)
    }

    @Test
    fun `should print success message when project is created successfully`() {
        //Given
        every {
            createProjectPresenter.createProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        //When
        createProjectCLI.createProject(ProjectsMock.CORRECT_PROJECT)

        //Then
        verify(exactly = 1) { createProjectPresenter.createProject(ProjectsMock.CORRECT_PROJECT) }
    }
}