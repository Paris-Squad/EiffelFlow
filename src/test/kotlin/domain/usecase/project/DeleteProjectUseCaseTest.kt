package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.UUID

class DeleteProjectUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = DeleteProjectUseCase(projectRepository)
    }

    @Test
    fun `should return the deleted project when project deleted successfully`() {
        runTest {
            // Given
            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
            coEvery {
                projectRepository.deleteProject(any())
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = deleteProjectUseCase.deleteProject(projectId)

            // Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            coVerify { projectRepository.deleteProject(any()) }
        }
    }

    @Test
    fun `should throw IOException when the project doesn't get deleted`() {
        runTest {
            // Given
            val differentProjectId = UUID.randomUUID()
            coEvery {
                projectRepository.deleteProject(any())
            } throws EiffelFlowException.IOException("unable to find correct project")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.deleteProject(differentProjectId)
            }
        }
    }
}