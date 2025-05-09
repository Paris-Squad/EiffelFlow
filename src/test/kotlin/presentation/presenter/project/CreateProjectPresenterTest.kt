package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.*

class CreateProjectPresenterTest {

    private val createProjectUseCase: CreateProjectUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var createProjectCli: CreateProjectCLI

    @BeforeEach
    fun setup() {
        createProjectCli =
            CreateProjectCLI(createProjectUseCase = createProjectUseCase, inputReader = inputReader, printer = printer)
    }

    @Test
    fun `should return the created Project when project is successfully created`() {
        //Given
        coEvery {
            createProjectUseCase.createProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        //When
        val result = createProjectCli.createProject(ProjectsMock.CORRECT_PROJECT)

        //Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)

    }

    @Test
    fun `should create project successfully with valid input`() {
        // Given
        val validAdminId = UUID.randomUUID()
        val expectedProject = Project(
            projectName = "Project1",
            projectDescription = "Desc",
            adminId = validAdminId
        )

        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", validAdminId.toString())
        coEvery { createProjectUseCase.createProject(any()) } returns expectedProject

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Project created successfully: $expectedProject") }
    }

    @Test
    fun `should print error when project name is empty`() {
        // Given
        every { inputReader.readString() } returns ""

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project name is null`() {
        // Given
        every { inputReader.readString() } returns null
        // When
        createProjectCli.createProjectInput()
        // Then
        verify(exactly = 1) { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project description is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "")

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when project description is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", null)

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when admin ID format is invalid`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", "uuid")

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Invalid admin ID format.") }
    }

    @Test
    fun `should print error when admin ID is missing or null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", "")

        // When
        createProjectCli.createProjectInput()

        // Then
        verify { printer.displayLn("Invalid admin ID format.") }
    }

    @Test
    fun `should print error when EiffelFlowException occurs`() {
        // Given
        val exception = EiffelFlowException.IOException("Failed to create the project")
        every { inputReader.readString() } returnsMany listOf(
            "Project1",
            "Desc",
            "123e4567-e89b-12d3-a456-426614174000"
        )
        coEvery {
            createProjectUseCase.createProject(any())
        } throws exception

        // When
        createProjectCli.createProjectInput()

        // Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }

    @Test
    fun `should print error when a general exception occurs`() {
        // Given
        val exception = Exception("Unexpected error")
        every { inputReader.readString() } returnsMany listOf(
            "Project1",
            "Desc",
            "123e4567-e89b-12d3-a456-426614174000"
        )
        coEvery {
            createProjectUseCase.createProject(any())
        } throws exception

        // When
        createProjectCli.createProjectInput()

        // Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }


}