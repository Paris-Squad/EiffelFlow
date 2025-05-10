package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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
    fun `should create project successfully with valid input`() {
        // Given
        val validAdminId = UUID.randomUUID()
        val projectName = "Project1"
        val projectDescription = "Desc"

        val expectedProject = Project(
            projectName = projectName,
            projectDescription = projectDescription,
            adminId = validAdminId
        )

        every { inputReader.readString() } returnsMany listOf(projectName, projectDescription, validAdminId.toString())
        coEvery { createProjectUseCase.createProject(any()) } returns expectedProject

        // When
        createProjectCli.start()

        // Then
        coVerify {
            createProjectUseCase.createProject(withArg { project ->
                assertThat(project.projectName).isEqualTo(projectName)
                assertThat(project.projectDescription).isEqualTo(projectDescription)
                assertThat(project.adminId).isEqualTo(validAdminId)
            })
        }
        verify { printer.displayLn("Project created successfully: $expectedProject") }
    }

    @Test
    fun `should print error when project name is empty`() {
        // Given
        every { inputReader.readString() } returns ""

        // When
        createProjectCli.start()

        // Then
        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project name is null`() {
        // Given
        every { inputReader.readString() } returns null
        // When
        createProjectCli.start()
        // Then
        verify(exactly = 1) { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project description is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "")

        // When
        createProjectCli.start()

        // Then
        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when project description is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", null)

        // When
        createProjectCli.start()

        // Then
        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when admin ID format is invalid`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", "uuid")

        // When
        createProjectCli.start()

        // Then
        verify { printer.displayLn("Invalid admin ID format.") }
    }

    @Test
    fun `should print error when admin ID is missing or null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", "")

        // When
        createProjectCli.start()

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
        createProjectCli.start()

        // Then
        verify {
            printer.displayLn("Something went wrong:Failed to create the project")
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
        createProjectCli.start()

        // Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }
}