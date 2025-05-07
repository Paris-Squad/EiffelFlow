/*
package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.GetProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.UUID
import kotlin.jvm.Throws

class GetProjectsUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var getProjectsUseCase: GetProjectUseCase

    @BeforeEach
    fun setup() {
        getProjectsUseCase = GetProjectUseCase(projectRepository)
    }

    @Test
    fun `should return list of Projects when there are project exist `() {
        runTest {
            // Given
            coEvery {
                projectRepository.getProjects()
            } returns listOf(ProjectsMock.CORRECT_PROJECT)

            // When
            val result = getProjectsUseCase.getProjects()

            // Then
            assertThat(result).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should throw NotFoundException when projects cannot be retrieved`() {
        runTest {
            // Given
            coEvery {
                projectRepository.getProjects()
            } throws EiffelFlowException.NotFoundException("Projects not found")

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                getProjectsUseCase.getProjects()
            }
        }
    }

    @Test
    fun `should return Project when project with given id exists`() {
        runTest {
            // Given
            val projectId = ProjectsMock.CORRECT_PROJECT.projectId
            coEvery {
                projectRepository.getProjectById(projectId)
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = getProjectsUseCase.getProjectById(projectId)

            // Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should throw NotFoundException when project with given id does not exist`() {
        runTest {
            // Given
            coEvery {
                projectRepository.getProjectById(any())
            } throws  EiffelFlowException.NotFoundException("Project not found")

            // When / Then
            assertThrows<EiffelFlowException.NotFoundException> {
                getProjectsUseCase.getProjectById(UUID.randomUUID())
            }
        }
    }
}

*/
