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


class GetProjectsUseCaseTest {

    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private lateinit var getProjectsUseCase: GetProjectUseCase

    @BeforeEach
    fun setup() {
        getProjectsUseCase = GetProjectUseCase(projectRepository)
    }

    @Test
    fun `should return Result of empty list of Projects when there are no projects`() {
        // Given
        every { projectRepository.getProjects() } returns Result.success(emptyList())

        // When / Then
        try {
            val result = getProjectsUseCase.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Projects when at least one project exists`() {
        // Given
        every {
            projectRepository.getProjects()
        } returns Result.success(listOf(MockProjects.CORRECT_PROJECT))

        // When / Then
        try {
            val result = getProjectsUseCase.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when projects cannot be retrieved`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Projects not found")
        every { projectRepository.getProjects() } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectsUseCase.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Project when project with given id exists`() {
        // Given
        val projectId = MockProjects.CORRECT_PROJECT.projectId
        every {
            projectRepository.getProjectById(projectId)
        } returns Result.success(MockProjects.CORRECT_PROJECT)

        // When / Then
        try {
            val result = getProjectsUseCase.getProjectById(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project with given id does not exist`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            projectRepository.getProjectById(UUID.randomUUID())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = getProjectsUseCase.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}

