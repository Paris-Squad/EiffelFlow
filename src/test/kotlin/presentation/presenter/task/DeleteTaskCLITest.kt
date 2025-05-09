package presentation.presenter.task
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import org.example.presentation.presenter.task.DeleteTaskCLI
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.task.DeleteTaskUseCase

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.validTask
import java.util.UUID

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
        coEvery { deleteTaskUseCase.deleteTask(taskId) } returns validTask

        // When
        val result = deleteTaskCLI.deleteTask(taskId)

        // Then
        assertThat(result).isEqualTo(validTask)
        coVerify(exactly = 1) { deleteTaskUseCase.deleteTask(taskId) }
    }

    @Test
    fun `should print success message when deletion success`() {
        // Given
        val uuid = validTask.taskId
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } returns validTask
        every { printer.displayLn(any()) } just Runs

        // When
        deleteTaskCLI.deleteTaskInput()

        // Then
        verify{ printer.displayLn("Task deleted successfully") }
    }

    @Test
    fun `should throw Exception when delete Task fail`() {
        // Given
        val taskId = UUID.randomUUID()
        coEvery { deleteTaskUseCase.deleteTask(taskId) } throws EiffelFlowException.IOException("task not found")

        // when / Then
        assertThrows<EiffelFlowException.IOException> {
            deleteTaskCLI.deleteTask(taskId)
        }
    }

    @Test
    fun `should print error when input is null`() {
        every { inputReader.readString() } returns null
        every { printer.displayLn(any()) } just Runs

        // When
        deleteTaskCLI.deleteTaskInput()

        // Then
        verify { printer.displayLn("Task ID cannot be empty.") }

    }

    @Test
    fun `should print error when input is whitespace`() {
        every { inputReader.readString() } returns "   "
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.deleteTaskInput()

        verify { printer.displayLn("Task ID cannot be empty.") }
    }

    @Test
    fun `should print error when input is not a valid UUID`() {
        every { inputReader.readString() } returns "uuid"
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.deleteTaskInput()

        verify { printer.displayLn("Invalid UUID format.") }
    }

    @Test
    fun `should print message when EiffelFlowException thrown`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } throws EiffelFlowException.NotFoundException("Task not found")
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.deleteTaskInput()

        // Then
        verify { printer.displayLn("Failed to delete the task: Task not found") }
    }


    @Test
    fun `should print unexpected error message when unknown exception is thrown`() {
        // Given
        val uuid = UUID.randomUUID()
        every { inputReader.readString() } returns uuid.toString()
        coEvery { deleteTaskUseCase.deleteTask(uuid) } throws IllegalStateException("Unexpected failure")
        every { printer.displayLn(any()) } just Runs

        deleteTaskCLI.deleteTaskInput()

        // Then
        verify { printer.displayLn("Enter Task ID to delete: ") }
        verify { printer.displayLn("An error occurred while deleting the task: Unexpected failure ") }
    }

}