package presentation.presenter.task

import io.mockk.*
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.task.DeleteTaskCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock.validTask
import java.util.*

class DeleteTaskCLITest {

    private val deleteTaskUseCase: DeleteTaskUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var deleteTaskCLI: DeleteTaskCLI

    @BeforeEach
    fun setUp() {
        deleteTaskCLI = DeleteTaskCLI(
            deleteTaskUseCase = deleteTaskUseCase,
            inputReader = inputReader,
            printer = printer
        )
    }

    @Test
    fun `should return the deleted task when deleteTask success`() {
        // Given
        val taskId = validTask.taskId
        every { inputReader.readString() } returns taskId.toString()
        coEvery { deleteTaskUseCase.deleteTask(taskId) } returns validTask

        // When
        deleteTaskCLI.start()

        // Then
        coVerify(exactly = 1) { deleteTaskUseCase.deleteTask(taskId) }
        verify { printer.displayLn("Task deleted successfully") }
    }

    @Test
    fun `should print success message when deletion success`() {
        // Given
        val uuid = validTask.taskId
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } returns validTask
        every { printer.displayLn(any()) } just Runs

        // When
        deleteTaskCLI.start()

        // Then
        verify { printer.displayLn("Task deleted successfully") }
    }

    @Test
    fun `should throw Exception when delete Task fail`() {
        // Given
        val taskId = UUID.randomUUID()
        coEvery { deleteTaskUseCase.deleteTask(taskId) } throws EiffelFlowException.IOException("task not found")
        deleteTaskCLI.start()

        verify {printer.displayLn(match { (it as String).contains( "An error occurred:") })}
    }

    @Test
    fun `should print error when input is null`() {
        every { inputReader.readString() } returns null
        every { printer.displayLn(any()) } just Runs

        // When
        deleteTaskCLI.start()

        // Then
        verify { printer.displayLn("Task ID cannot be empty.") }

    }

    @Test
    fun `should print error when input is whitespace`() {
        every { inputReader.readString() } returns "   "
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.start()

        verify { printer.displayLn("Task ID cannot be empty.") }
    }

    @Test
    fun `should print error when input is not a valid UUID`() {
        every { inputReader.readString() } returns "uuid"
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.start()

        verify { printer.displayLn("An error occurred: Invalid UUID string: uuid") }
    }

    @Test
    fun `should print message when EiffelFlowException thrown`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } throws EiffelFlowException.NotFoundException("Task not found")
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.start()

        // Then
        verify { printer.displayLn("Task not found") }
    }


    @Test
    fun `should print unexpected error message when unknown exception is thrown`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } throws IllegalStateException("Unexpected failure")
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.start()

        // Then
        verify { printer.displayLn("An error occurred: Unexpected failure") }
    }

}