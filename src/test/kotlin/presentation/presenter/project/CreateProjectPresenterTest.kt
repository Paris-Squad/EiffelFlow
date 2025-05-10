package presentation.presenter.project

import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.project.CreateProjectCLI
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.delay
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import utils.UserMock
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException


class CreateProjectPresenterTest {

    private val createProjectUseCase: CreateProjectUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var createProjectCli: CreateProjectCLI

    @BeforeEach
    fun setup() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
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
            projectName = projectName,
            projectDescription = projectDescription,
            adminId = validAdminId
        )

        every { inputReader.readString() } returnsMany listOf(projectName, projectDescription, validAdminId.toString())
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc")
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
        createProjectCli.start()

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
    fun `should print error when EiffelFlowException occurs during creation`() {
        //Given
        val exception = EiffelFlowException.IOException("Failed to create the project")
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc")
        coEvery { createProjectUseCase.createProject(any()) } throws exception

        // When
        createProjectCli.start()
        //When
        createProjectCli.createProjectInput()

        //Then
        verify {
            printer.displayLn("An error occurred: ${exception.message}")
        }
    }

    @Test
    fun `should print error when generic Exception occurs during creation`() {
        //Given
        val exception = Exception("Unexpected error")
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc")
        coEvery { createProjectUseCase.createProject(any()) } throws exception

        //When
        createProjectCli.createProjectInput()
        // When
        createProjectCli.start()

        // Then
        verify {
            printer.displayLn("Something went wrong:Failed to create the project")
        }
    }
    @Test
    fun `should handle multiple suspension points in createProject`() {
        // Given
        val expectedProject = ProjectsMock.CORRECT_PROJECT
        coEvery {
            createProjectUseCase.createProject(any())
        } coAnswers {
            delay(50)
            delay(50)
            expectedProject
        }
        every { inputReader.readString() } returnsMany listOf("Project1", "Desc")

        // When
        createProjectCli.start()

        // Then
        verify { printer.displayLn("Project created successfully: $expectedProject") }
    }
}