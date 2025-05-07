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
        // Given
        mockkStatic("kotlin.io.ConsoleKt")
        every { readln() } returns "02ad4499-5d4c-4450-8fd1-8294f1bb5748"
        val projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748")
        every {
            deleteProjectPresenter.deleteProject(any())
        } returns ProjectsMock.CORRECT_PROJECT

        // When
        deleteProjectCLI(projectId)

        // Then
        verify(exactly = 1) { deleteProjectPresenter.deleteProject(any()) }
    }
}