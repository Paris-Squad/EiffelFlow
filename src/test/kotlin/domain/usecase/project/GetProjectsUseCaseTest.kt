package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.GetProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockProjects
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
    fun `should return Result of Projects when there are project exist `() {
        // Given
        every {
            projectRepository.getProjects()
        } returns Result.success(listOf(MockProjects.CORRECT_PROJECT))

        // When
        val result = getProjectsUseCase.getProjects()

        // Then
        assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockProjects.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when projects cannot be retrieved`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Projects not found")
        every { projectRepository.getProjects() } returns Result.failure(exception)

        // When
        val result = getProjectsUseCase.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `should return Result of Project when project with given id exists`() {
        // Given
        val projectId = MockProjects.CORRECT_PROJECT.projectId
        every {
            projectRepository.getProjectById(projectId)
        } returns Result.success(MockProjects.CORRECT_PROJECT)

        // When
        val result = getProjectsUseCase.getProjectById(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(MockProjects.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            projectRepository.getProjectById(any())
        } returns Result.failure(exception)

        // When
        val result = getProjectsUseCase.getProjectById(UUID.randomUUID())

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}

