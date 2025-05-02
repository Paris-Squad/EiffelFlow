package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.presentation.presenter.project.DeleteProjectPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.UUID

class DeleteProjectPresenterTest {

    private val deleteProjectUseCase : DeleteProjectUseCase = mockk()
    private lateinit var deleteProjectPresenter: DeleteProjectPresenter

    @BeforeEach
    fun setUp(){
        deleteProjectPresenter = DeleteProjectPresenter(deleteProjectUseCase)
    }

    @Test
    fun `should return the deleted project when the deleteProject return success`(){
        try {
            // Given
            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
            every { deleteProjectUseCase.deleteProject(any()) } returns Result.success(project)

            // When
            val result = deleteProjectPresenter.deleteProject(projectId)

            // Then
            assertThat(result.isSuccess).isTrue()
            verify(exactly = 1) { deleteProjectUseCase.deleteProject(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return UnableToFindTheCorrectProject exception when deleteProject returns failure`(){
        try {
            // Given
            val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
            every { deleteProjectUseCase.deleteProject(any()) } returns Result.success(project)

            // When
            val result = deleteProjectPresenter.deleteProject(differentProjectId)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.UnableToFindTheCorrectProject::class.java
            )

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }



    companion object{
        val project = ProjectsMock.CORRECT_PROJECT
    }

}