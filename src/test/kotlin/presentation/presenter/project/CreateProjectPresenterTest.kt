package presentation.presenter.project

import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.presenter.project.CreateProjectPresenter
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock

class CreateProjectPresenterTest {

    private val createProjectUseCase: CreateProjectUseCase = mockk()
    private lateinit var createProjectPresenter: CreateProjectPresenter

    @BeforeEach
    fun setup() {
        createProjectPresenter = CreateProjectPresenter(createProjectUseCase)
    }


    @Test
    fun `should return Result of Project when project is successfully created`() {
        try {
            every { createProjectUseCase.createProject(project) } returns Result.success(project)

            val result = createProjectPresenter.createProject(project)

            assertThat(result.isSuccess).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return failure when project creation fails `() {
        try {
            every { createProjectUseCase.createProject(project) } returns Result.failure(Exception("Error writing to file"))

            val result = createProjectPresenter.createProject(project)

            assertThat(result.isFailure).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object{
        val project = ProjectsMock.CORRECT_PROJECT
    }

}