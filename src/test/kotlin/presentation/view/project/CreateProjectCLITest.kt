package presentation.view.project

import io.mockk.mockk
import org.example.presentation.presenter.project.CreateProjectPresenter
import org.example.presentation.view.project.CreateProjectCLI
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import org.junit.jupiter.api.Test
import utils.MockProjects

class CreateProjectCLITest {

    private val createProjectPresenter: CreateProjectPresenter = mockk()
    private lateinit var createProjectCLI: CreateProjectCLI


    @BeforeEach
    fun setup() {
        createProjectCLI = CreateProjectCLI(createProjectPresenter)
    }

    @Test
    fun `should print success message when project is created successfully`() {

        every { createProjectPresenter.createProject(project) } returns Result.success(project)

        try {
            val result = createProjectCLI.createProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print error message when project creation fails due to an exception`() {

        every { createProjectPresenter.createProject(project) } returns Result.failure(Exception("Error creating project"))

        try {
            val result = createProjectCLI.createProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object{
        val project = MockProjects.CORRECT_PROJECT
    }

}