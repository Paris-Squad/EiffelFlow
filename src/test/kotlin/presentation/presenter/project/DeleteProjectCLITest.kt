package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.DeleteProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.*

class DeleteProjectCLITest {

    private val deleteProjectUseCase: DeleteProjectUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var deleteProjectCLI: DeleteProjectCLI

    @BeforeEach
    fun setUp() {
        deleteProjectCLI = DeleteProjectCLI(
            deleteProjectUseCase = deleteProjectUseCase,
            inputReader = inputReader,
            printer = printer
        )
    }

    @Test
    fun `should return the deleted project when deleteProject success`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        coEvery { deleteProjectUseCase.deleteProject(projectId) } returns ProjectsMock.CORRECT_PROJECT

        // When
        val result = deleteProjectCLI.deleteProject(projectId)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        coVerify(exactly = 1) { deleteProjectUseCase.deleteProject(projectId) }
    }

    @Test
    fun `should print success message when deletion success`() {
        // Given
        val uuid = ProjectsMock.CORRECT_PROJECT.projectId
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteProjectUseCase.deleteProject(uuid) } returns ProjectsMock.CORRECT_PROJECT
        every { printer.displayLn(any()) } just Runs

        // When
        deleteProjectCLI.deleteProjectInput()

        // Then
        verify { printer.displayLn("Project deleted successfully ${ProjectsMock.CORRECT_PROJECT}") }
    }

    @Test
    fun `should throw Exception when delete Project fail`() {
        // Given
        val projectId = UUID.randomUUID()
        coEvery { deleteProjectUseCase.deleteProject(projectId) } throws EiffelFlowException.IOException("Project not found")

        // when / Then
        assertThrows<EiffelFlowException.IOException> {
            deleteProjectCLI.deleteProject(projectId)
        }
    }

    @Test
    fun `should print error when input is null`() {
        every { inputReader.readString() } returns null
        every { printer.displayLn(any()) } just Runs

        // When
        deleteProjectCLI.deleteProjectInput()

        // Then
        verify { printer.displayLn("Project ID cannot be empty.") }

    }

    @Test
    fun `should print error when input is whitespace`() {
        every { inputReader.readString() } returns "   "
        every { printer.displayLn(any()) } just Runs

        deleteProjectCLI.deleteProjectInput()

        verify { printer.displayLn("Project ID cannot be empty.") }
    }

    @Test
    fun `should print error when input is not a valid UUID`() {
        every { inputReader.readString() } returns "uuid"
        every { printer.displayLn(any()) } just Runs

        deleteProjectCLI.deleteProjectInput()

        verify {
            printer.displayLn("An error occurred: Invalid UUID string: uuid")
        }
    }

    @Test
    fun `should print message when EiffelFlowException thrown`() {
        // Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery {
            deleteProjectUseCase.deleteProject(uuid)
        } throws exception
        every { printer.displayLn(any()) } just Runs

        deleteProjectCLI.deleteProjectInput()

        // Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }

    @Test
    fun `should print unexpected error message when unknown exception is thrown`() {
        // Given
        val exception = IllegalStateException("Unexpected failure")
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery {
            deleteProjectUseCase.deleteProject(uuid)
        } throws exception
        // every { printer.display(any()) } just Runs
        every { printer.displayLn(any()) } just Runs

        deleteProjectCLI.deleteProjectInput()

        // Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }

}