/*
package domain.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun `should return the deleted project when project gets deleted successfully`() {
        // Given
        val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
        every { projectRepository.deleteProject(any()) } returns Result.success(correctProject)

        // When
        val result = deleteProjectUseCase.deleteProject(projectId)

        // Then
        assertThat(result.isSuccess).isTrue()
        verify { projectRepository.deleteProject(any()) }

    }

    @Test
    fun `should return an UnableToFindTheCorrectProject exception when the project doesn't get deleted`() {
        // Given
        val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { projectRepository.deleteProject(any()) } returns
                Result.failure(
                    EiffelFlowException.IOException("unable to find correct project")
                )

        // When
        val result = projectRepository.deleteProject(differentProjectId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(
            EiffelFlowException.IOException::class.java
        )
    }

    companion object {
        private val correctProject = ProjectsMock.CORRECT_PROJECT
    }

}*/
