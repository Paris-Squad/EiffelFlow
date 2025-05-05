/*
package presentation.view.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.example.presentation.presenter.project.CreateProjectPresenter
import org.example.presentation.presenter.project.DeleteProjectPresenter
import org.example.presentation.view.project.CreateProjectCLI
import org.example.presentation.view.project.DeleteProjectCLI
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.UUID

class DeleteProjectCLITest {
    private val deleteProjectPresenter: DeleteProjectPresenter = mockk()
    private lateinit var deleteProjectCLI: DeleteProjectCLI


    @BeforeEach
    fun setup() {
        deleteProjectCLI = DeleteProjectCLI(deleteProjectPresenter)
    }

    @Test
    fun `should print success message when project is deleted successfully`() {
        try {
            // Given
            mockkStatic("kotlin.io.ConsoleKt")
            every { readln() } returns "02ad4499-5d4c-4450-8fd1-8294f1bb5748"
            val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
            every { deleteProjectPresenter.deleteProject(any()) } returns Result.success(project)

            // When
            deleteProjectCLI(projectId)

            // Then
            verify { deleteProjectPresenter.deleteProject(any()) }
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should print error message when project creation fails due to an exception`() {
        try {
            // Given
            mockkStatic("kotlin.io.ConsoleKt")
            every { readln() } returns "11111111-1111-1111-1111-111111111111"
            val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
            every { deleteProjectPresenter.deleteProject(any()) } returns Result.failure(
                Exception("Error Deleting project"))

            // When
            deleteProjectCLI(differentProjectId)

            // Then
            assertThat(deleteProjectPresenter.deleteProject(differentProjectId).exceptionOrNull()).isInstanceOf(
                Exception::class.java
            )
            verify { deleteProjectPresenter.deleteProject(any()) }
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object{
        val project = ProjectsMock.CORRECT_PROJECT
    }

}*/
