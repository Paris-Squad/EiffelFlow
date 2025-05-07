package domain.usecase.project

import io.mockk.mockk
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.CreateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock

class CreateProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setup() {
        createProjectUseCase = CreateProjectUseCase(projectRepository)
    }

    @Test
    fun `should return Created Project when project is created successfully`() {
        runTest {
            //Given
            coEvery {
                projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
            } returns ProjectsMock.CORRECT_PROJECT

            //When
            val result = createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)

            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            coVerify(exactly = 1) { projectRepository.createProject(ProjectsMock.CORRECT_PROJECT) }
        }
    }

    @Test
    fun `should throw IOException when creating project fails`() {
        runTest {
            //Given
            coEvery {
                projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
            } throws EiffelFlowException.IOException("Failed to create project")

            //When / Then
            assertThrows<EiffelFlowException.IOException> {
                createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
            }
        }
    }
}