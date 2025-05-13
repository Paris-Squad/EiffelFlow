package presentation.project

import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.example.domain.usecase.project.CreateProjectStateUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectStateCLI
import org.junit.jupiter.api.BeforeEach
import utils.ProjectsMock.updatedProject
import java.util.UUID
import kotlin.test.Test

class CreateProjectStateCLITest {
    private val createProjectStateUseCase: CreateProjectStateUseCase = mockk()
    private lateinit var createProjectStateCLI : CreateProjectStateCLI
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk()
    private val validProjectId = UUID.randomUUID()

    @BeforeEach
    fun setup(){
        createProjectStateCLI= CreateProjectStateCLI(createProjectStateUseCase,inputReader,printer)
    }

    @Test
    fun `should create new project state when user enter a valid Id and state name`() {
        // Given
        val newStateName = "newState"
        val projectMock = updatedProject.copy(projectId = validProjectId)

        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns newStateName
        coEvery { createProjectStateUseCase.execute(validProjectId, any()) } returns projectMock

        // When
        createProjectStateCLI.start(validProjectId)

        // Then
        verify {
            printer.displayLn("Create New Project State")
            printer.displayLn("Project state created successfully.")
            printer.displayLn("Project ID  : ${projectMock.projectId}")
            printer.displayLn("State(s)    : ${projectMock.taskStates}")
        }
    }

    @Test
    fun `should show state name cannot be empty when user enter empty or blank name input`(){
        // Given
        every { printer.displayLn(any()) } just runs
            every { inputReader.readString() } returnsMany  listOf(""," ")

        // When
        createProjectStateCLI.start(validProjectId)

        // Then
        verify {
            printer.displayLn("Create New Project State")
            printer.displayLn("Enter new state name:")
            printer.displayLn("State name cannot be empty or blank.")
        }
    }
}