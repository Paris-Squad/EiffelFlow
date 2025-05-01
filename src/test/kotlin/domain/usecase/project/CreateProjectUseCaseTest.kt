package domain.usecase.project

import io.mockk.mockk
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.CreateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import io.mockk.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import org.example.domain.model.exception.EiffelFlowException
import org.junit.jupiter.api.Test
import utils.ProjectsMock


class CreateProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setup() {
        createProjectUseCase = CreateProjectUseCase(projectRepository)
    }

    @Test
    fun `should return Result of Project when project is created successfully`() {
        try {

            every { projectRepository.createProject(correctProject) } returns Result.success(correctProject)

            val result = createProjectUseCase.createProject(correctProject)

            assertThat(result.isSuccess).isTrue()
            verify(exactly = 1) { projectRepository.createProject(correctProject) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `should return Result of Failure when creating project fails`() {
        try {

            every { projectRepository.createProject(correctProject) } returns Result.failure(EiffelFlowException.ProjectCreationException("Failed to create project"))

            val result = createProjectUseCase.createProject(correctProject)

            assertThat(result.isFailure).isTrue()
            verify(exactly = 1) { projectRepository.createProject(correctProject) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    companion object{
        private val correctProject = ProjectsMock.CORRECT_PROJECT
    }
}