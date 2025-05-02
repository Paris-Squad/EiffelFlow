package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.GetProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun `should return Result of Projects when there are project exist `() {
        // Given
        every {
            projectRepository.getProjects()
        } returns Result.success(listOf(ProjectsMock.CORRECT_PROJECT))

        // When
        val result = getProjectsUseCase.getProjects()

        // Then
        assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when projects cannot be retrieved`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Projects not found")
        every { projectRepository.getProjects() } returns Result.failure(exception)

        // When
        val result = getProjectsUseCase.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `should return Result of Project when project with given id exists`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        every {
            projectRepository.getProjectById(projectId)
        } returns Result.success(ProjectsMock.CORRECT_PROJECT)

        // When
        val result = getProjectsUseCase.getProjectById(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        every {
            projectRepository.getProjectById(any())
        } returns Result.failure(exception)

        // When
        val result = getProjectsUseCase.getProjectById(UUID.randomUUID())

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}

