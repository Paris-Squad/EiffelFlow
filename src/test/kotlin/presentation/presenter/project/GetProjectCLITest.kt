package presentation.presenter.project

import io.mockk.*
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.GetProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.*
import kotlin.test.assertEquals

class GetProjectCLITest {

    private val getProjectUseCase: GetProjectUseCase = mockk(relaxed = true)
    private lateinit var getProjectCLI: GetProjectCLI
    private val printer: Printer = mockk(relaxed = true)
    private val inputReader: InputReader = mockk()

    @BeforeEach
    fun setup() {
        getProjectCLI =
            GetProjectCLI(getProjectUseCase = getProjectUseCase, printer = printer, inputReader = inputReader)
    }

    // region get projects
    @Test
    fun `should display list of projects when projects are available`() {
        // Given
        val projects = listOf(ProjectsMock.CORRECT_PROJECT)

        coEvery { getProjectUseCase.getProjects() } returns projects

        // When
        getProjectCLI.start()

        // Then
        verify {
            printer.displayLn("1. ${ProjectsMock.CORRECT_PROJECT.projectName} - ${ProjectsMock.CORRECT_PROJECT.projectDescription}")
        }
    }

    @Test
    fun `should display no projects found when list is empty`() {
        // Given
        coEvery { getProjectUseCase.getProjects() } returns emptyList()

        // When
        getProjectCLI.start()

        // Then
        verify { printer.displayLn("No projects found.") }
    }

    @Test
    fun `should throw Exception when unknown exception occurs during retrieving projects`() {
        // Given
        coEvery {
            getProjectUseCase.getProjects()
        } throws RuntimeException("Unexpected")

        //When
        getProjectCLI.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Unexpected")
        }
    }
    // end region


    // region getProjectById
    @Test
    fun `should return Project when project with given id exists`() {
        // Given
        val projectId = ProjectsMock.CORRECT_PROJECT.projectId
        every { inputReader.readString() } returns projectId.toString()
        coEvery { getProjectUseCase.getProjectById(projectId) } returns ProjectsMock.CORRECT_PROJECT

        // When
        getProjectCLI.displayProjectById()

        // Then
        verify {
            printer.displayLn("Project details : ${ProjectsMock.CORRECT_PROJECT}")
        }
    }

    @Test
    fun `should display error when input is blank`() {
        // Given
        every { inputReader.readString() } returns "  "

        // When
        getProjectCLI.displayProjectById()

        // Then
        verifySequence {
            printer.displayLn("Enter project ID : ")
            printer.displayLn("Project ID cannot be empty.")
        }
    }

    @Test
    fun `should display error when input is null`() {
        // Given
        every { inputReader.readString() } returns null

        // When
        getProjectCLI.displayProjectById()

        // Then
        verifySequence {
            printer.displayLn("Enter project ID : ")
            printer.displayLn("Project ID cannot be empty.")
        }
    }

    @Test
    fun `should display error on invalid UUID format`() {
        // Given
        every { inputReader.readString() } returns "invalid-uuid"

        // When
        getProjectCLI.displayProjectById()

        // Then
        verify { printer.displayLn("Invalid UUID format.") }
    }

    @Test
    fun `should throw EiffelFlowException when getProjectById fails`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { getProjectUseCase.getProjectById(uuid) } throws EiffelFlowException.NotFoundException("Network issue")

        // Then
        assertThrows<EiffelFlowException.NotFoundException> {
            getProjectCLI.displayProjectById()
        }
    }

    @Test
    fun `should throw Exception when unexpected error occurs during getProjectById`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { getProjectUseCase.getProjectById(uuid) } throws Exception("unexpected error")

        // Then
        val exception = assertThrows<RuntimeException> {
            getProjectCLI.displayProjectById()
        }

        assertEquals("An error occurred while retrieving the project: unexpected error", exception.message)
    }
    // end region


}
