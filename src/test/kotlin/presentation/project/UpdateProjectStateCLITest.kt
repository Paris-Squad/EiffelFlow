package presentation.project

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.UpdateProjectStateUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.UpdateProjectStateCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock.updatedProject
import java.util.UUID

class UpdateProjectStateCLITest {

    private val updateProjectStateUseCase: UpdateProjectStateUseCase = mockk()
    private lateinit var updateProjectStateCLI: UpdateProjectStateCLI
    private val printer: Printer = mockk(relaxed = true)
    private val inputReader: InputReader = mockk()
    private val validProjectId = UUID.randomUUID()
    private val validStateId = UUID.randomUUID()
    private val newStateName = "newState"

    @BeforeEach
    fun setup() {
        updateProjectStateCLI = UpdateProjectStateCLI(updateProjectStateUseCase, inputReader, printer)
    }

    @Test
    fun `should update project state when user gets a valid Id and state name`() {
        // Given
        val projectMock = updatedProject.copy(
            projectId = validProjectId,
            taskStates = listOf(TaskState(validStateId, newStateName))
        )

        every { inputReader.readString() } returns newStateName
        coEvery { updateProjectStateUseCase.execute(validProjectId, validStateId, any()) } returns projectMock

        // When
        updateProjectStateCLI.start(validProjectId, validStateId)

        // Then
        verifySequence {
            printer.displayLn("Update Existing Task State")
            printer.displayLn("Enter new state name:")
            printer.displayLn("Project state updated successfully.")
            printer.displayLn("Project ID  : $validProjectId")
            printer.displayLn("New State(s): [TaskState(stateId=$validStateId, name=$newStateName)]")
        }
    }

    @Test
    fun `should show state name cannot be empty when user enters empty or blank input`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("", " ")

        // When
        updateProjectStateCLI.start(validProjectId, validStateId)

        // Then
        verify {
            printer.displayLn("Update Existing Task State")
            printer.displayLn("Enter new state name:")
            printer.displayLn("State name cannot be empty or blank.")
        }
    }
}
