package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.project.CreateProjectCLI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.delay
import org.example.data.utils.SessionManger
import org.example.domain.model.Project
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import utils.UserMock



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
    fun `should create project successfully with valid input`() {
        // Given
        val projectName = "Project1"
        val projectDescription = "Desc"

        val expectedProject = Project(
            projectName = projectName,
            projectDescription = projectDescription,
            adminId = SessionManger.getUser().userId
        )

        every { inputReader.readString() } returnsMany listOf(projectName, projectDescription)
        coEvery { createProjectUseCase.createProject(any()) } returns expectedProject

        // When
        createProjectCli.start()

        // Then
        coVerify {
            createProjectUseCase.createProject(withArg { project ->
                assertThat(project.projectName).isEqualTo(projectName)
                assertThat(project.projectDescription).isEqualTo(projectDescription)
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
        coVerify { createProjectUseCase.createProject(any()) }
        verify { printer.displayLn("Project created successfully: $expectedProject") }
    }
}