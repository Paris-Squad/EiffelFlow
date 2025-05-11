package presentation.task

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.example.domain.usecase.task.GetTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.task.GetTaskCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.TaskMock

class GetTaskCLITest {
    private val getTaskUseCase: GetTaskUseCase = mockk(relaxed = true)
    private lateinit var getTaskCLI: GetTaskCLI
    private val printer: Printer = mockk(relaxed = true)
    private val inputReader: InputReader = mockk()

    @BeforeEach
    fun setup(){
        getTaskCLI = GetTaskCLI(getTaskUseCase = getTaskUseCase, printer = printer, inputReader = inputReader)
    }

    @Test
    fun `should display list of Tasks when Tasks are available`(){
        //Given
        val tasks = listOf(TaskMock.validTask)

        coEvery { getTaskUseCase.getTasks() } returns tasks

        // When
        getTaskCLI.start()

        // Then
        verify {
            printer.displayLn("1.${TaskMock.validTask.title} - ${TaskMock.validTask.description}")
        }
    }
    @Test
    fun `should display no Tasks found when list is empty`() {
        //Given
        coEvery { getTaskUseCase.getTasks() } returns emptyList()

        // When
        getTaskCLI.start()

        // Then
        verify { printer.displayLn("No Tasks found.") }
    }

    @Test
    fun `should throw Exception when unknown exception occurs during retrieving Tasks`() {
        // Given
        coEvery {
            getTaskUseCase.getTasks()
        } throws RuntimeException("Unexpected")

        //When
        getTaskCLI.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Unexpected")
        }
    }
    @Test
    fun `should return Task when Task with given id exists`() {
        // Given
        val taskId = TaskMock.validTask.taskId
        every { inputReader.readString() } returns taskId.toString()
        coEvery { getTaskUseCase.getTaskByID(taskId) } returns TaskMock.validTask

        // When
        getTaskCLI.displayTaskById()

        // Then
        verify {
            printer.displayLn("Task details : ${TaskMock.validTask}")
        }
    }
    @Test
    fun `should display error when input is blank`() {
        //Given
        every { inputReader.readString() } returns " "

        // When
        getTaskCLI.displayTaskById()

        // Then
        verifySequence {
            printer.displayLn("Enter Task ID : ")
            printer.displayLn("Task ID cannot be empty.")
        }
    }
    @Test
    fun `should display error when input is null`() {
        //Given
        every { inputReader.readString() } returns null

        // When
        getTaskCLI.displayTaskById()

        // Then
        verifySequence {
            printer.displayLn("Enter Task ID : ")
            printer.displayLn("Task ID cannot be empty.")
        }
    }
    @Test
    fun `should display error on invalid UUID format`() {
        // Given
        every { inputReader.readString() } returns "invalid-uuid"

        // When
        getTaskCLI.displayTaskById()

        // Then
        verify { printer.displayLn("Invalid UUID format.") }
    }
}