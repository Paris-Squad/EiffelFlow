package presentation.project

import io.mockk.*
import org.example.domain.usecase.project.DeleteProjectStateUseCase
import org.example.presentation.io.Printer
import org.example.presentation.project.DeleteProjectStateCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock.updatedProject
import java.util.UUID

class DeleteProjectStateCLITest {
    private val deleteProjectStateUseCase: DeleteProjectStateUseCase = mockk()
    private lateinit var deleteProjectStateCLI: DeleteProjectStateCLI
    private val printer: Printer = mockk()
    private val validProjectId = UUID.randomUUID()
    private val validStateId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        deleteProjectStateCLI = DeleteProjectStateCLI(deleteProjectStateUseCase, printer)
    }

    @Test
    fun `should delete project state when valid ProjectID and StateID`() {
        // Given
        val projectMock = updatedProject.copy(projectId = validProjectId)

        every { printer.displayLn(any()) } just runs
        coEvery { deleteProjectStateUseCase.execute(validProjectId, any()) } returns projectMock

        // When
        deleteProjectStateCLI.start(validProjectId, validStateId)

        // Then
        verify {
            printer.displayLn("Delete State Project")
            printer.displayLn("Project state deleted successfully.")
            printer.displayLn("Project ID  : ${projectMock.projectId}")
            printer.displayLn("State(s)    : ${projectMock.taskStates}")
        }
    }
}
