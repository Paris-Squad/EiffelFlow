package presentation.project

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectStateCLI
import org.example.presentation.project.DeleteProjectStateCLI
import org.example.presentation.project.ManageProjectStateCLI
import org.example.presentation.project.UpdateProjectStateCLI
import org.junit.jupiter.api.BeforeEach
import utils.ProjectsMock
import java.util.UUID
import kotlin.test.Test

class ManageProjectStateCLITest {
    private lateinit var getProjectUseCase: GetProjectUseCase
    private lateinit var createProjectStateCLI: CreateProjectStateCLI
    private lateinit var updateProjectStateCLI: UpdateProjectStateCLI
    private lateinit var deleteProjectStateCLI: DeleteProjectStateCLI
    private lateinit var inputReader: InputReader
    private lateinit var printer: Printer
    private lateinit var manageProjectStateCLI: ManageProjectStateCLI

    private val dummyProject = ProjectsMock.CORRECT_PROJECT.copy(
        projectId = UUID.randomUUID(),
        projectName = "Test Project",
        taskStates = emptyList<TaskState>()
    )

    private val dummyProjectWithStates = dummyProject.copy(
        taskStates = listOf(
            TaskState(UUID.randomUUID(), "To Do"),
            TaskState(UUID.randomUUID(), "In Progress")
        )
    )

    @BeforeEach
    fun setUp() {
        getProjectUseCase = mockk()
        createProjectStateCLI = mockk()
        updateProjectStateCLI = mockk()
        deleteProjectStateCLI = mockk()
        inputReader = mockk(relaxed = true)
        printer = mockk(relaxed = true)

        manageProjectStateCLI = ManageProjectStateCLI(
            getProjectUseCase,
            createProjectStateCLI,
            updateProjectStateCLI,
            deleteProjectStateCLI,
            inputReader,
            printer
        )
    }

    @Test
    fun `should print no projects found when project list is empty`() = runTest {
        // Given
        coEvery { getProjectUseCase.getProjects() } returns emptyList()
        every { printer.displayLn(any()) } just runs
        // When
        manageProjectStateCLI.start()
        // Then
        verify {
            printer.displayLn("✦•──────────────────────────────| Manage Project Task States |──────────────────────────────•✦")
            printer.displayLn("No projects found.")
        }
    }

    @Test
    fun `should print invalid project selection when input is invalid`() = runTest {
        // Given
        val projects = listOf(dummyProject)
        coEvery { getProjectUseCase.getProjects() } returns projects
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "x"
        // When
        manageProjectStateCLI.start()
        // Then
        verify {
            printer.displayLn("Invalid project selection.")
        }
    }

    @Test
    fun `should call createProjectStateCLI when user selects option 1`() = runTest {
        // Given
        val project = dummyProject
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "1"
        every { createProjectStateCLI.start(project.projectId) } just runs
        // When
        manageProjectStateCLI.start()
        // Then
        verify { createProjectStateCLI.start(project.projectId) }
    }

    @Test
    fun `should update state when user selects valid state`() = runTest {
        // Given
        val project = dummyProjectWithStates
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "2" // update option then state number
        every { updateProjectStateCLI.start(project.projectId, any()) } just runs
        // When
        manageProjectStateCLI.start()
        // Then
        verify { updateProjectStateCLI.start(project.projectId, project.taskStates[1].stateId) }
    }

    @Test
    fun `should print no states to update when project has no states`() = runTest {
        // Given
        val project = dummyProject.copy(taskStates = emptyList())
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "2"
        // When
        manageProjectStateCLI.start()
        // Then
        verify { printer.displayLn("No states to update.") }
    }

    @Test
    fun `should delete state when user selects valid state`() = runTest {
        // Given
        val project = dummyProjectWithStates
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "3" andThen "1"
        every { deleteProjectStateCLI.start(project.projectId, any()) } just runs
        // When
        manageProjectStateCLI.start()
        // Then
        verify { deleteProjectStateCLI.start(project.projectId, project.taskStates[0].stateId) }
    }

    @Test
    fun `should print invalid action selected when input is out of range`() = runTest {
        // Given
        val project = dummyProject
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "5"
        // When
        manageProjectStateCLI.start()
        // Then
        verify { printer.displayLn("Invalid action selected.") }
    }

    @Test
    fun `should display task states of selected project`() = runTest {
        // Given
        val project = dummyProjectWithStates
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "5"
        // When
        manageProjectStateCLI.start()
        // Then
        verify {
            printer.displayLn("Project '${project.projectName}' States:")
            printer.displayLn(match { (it as String).contains("To Do") })
            printer.displayLn(match { (it as String).contains("In Progress") })
        }
    }

    @Test
    fun `should print no states to delete when project has no states`() = runTest {
        // Given
        val project = dummyProject.copy(taskStates = emptyList())
        coEvery { getProjectUseCase.getProjects() } returns listOf(project)
        every { printer.displayLn(any()) } just runs
        every { inputReader.readString() } returns "1" andThen "3"
        // When
        manageProjectStateCLI.start()

        // Then
        verify {
            printer.displayLn("No states to delete.")
        }
    }
}
