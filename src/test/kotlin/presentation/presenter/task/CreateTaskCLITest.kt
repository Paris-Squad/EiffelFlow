package presentation.presenter.task

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.task.CreateTaskCLI
import org.junit.jupiter.api.BeforeEach
import utils.ProjectsMock.CORRECT_PROJECT
import utils.TaskMock.validTask
import utils.UserMock.validUser
import java.io.IOException
import kotlin.test.Test

class CreateTaskCLITest {
    private val createTaskUseCase: CreateTaskUseCase = mockk()
    private val getProjectUseCase: GetProjectUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var createTaskCli: CreateTaskCLI

    @BeforeEach
    fun setup() {
        createTaskCli = CreateTaskCLI(
            createTaskUseCase = createTaskUseCase,
            getProjectUseCase = getProjectUseCase,
            inputReader = inputReader,
            printer = printer
        )
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns validUser
    }

    @Test
    fun `should return the created task when task is successfully created`() {
        runTest {
            //Given
            every { inputReader.readString() } returnsMany listOf("valid title", "Valid Description", "1", "1")
            coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
            coEvery { createTaskUseCase.createTask(any()) } returns validTask

            //When
            createTaskCli.start()

            // Then
            verify {
                printer.displayLn(match { (it as String).contains("Task created successfully") })
            }

        }
    }

    @Test
    fun `should not create task when projects are empty`() {
        runTest {
            //Given
            every { inputReader.readString() } returnsMany listOf("valid title", "Valid Description", "1", "1")
            coEvery { getProjectUseCase.getProjects() } returns listOf()
            coEvery { createTaskUseCase.createTask(any()) } returns validTask

            //When
            createTaskCli.start()

            // Then
            verify {
                printer.displayLn("Enter task title:")
            }

        }
    }

    @Test
    fun `should print error when task name is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("", "valid title", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { createTaskUseCase.createTask(any()) } returns validTask


        // When
        createTaskCli.start()

        // Then
        verify(exactly = 1) { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when task name is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf(null, "valid title", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { createTaskUseCase.createTask(any()) } returns validTask


        // When
        createTaskCli.start()

        // Then
        verify(exactly = 1) { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when task description is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("valid title", "", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { createTaskUseCase.createTask(any()) } returns validTask


        // When
        createTaskCli.start()

        // Then
        verify(exactly = 1) { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when task description is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("valid title", null, "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { createTaskUseCase.createTask(any()) } returns validTask


        // When
        createTaskCli.start()

        // Then
        verify(exactly = 1) { printer.displayLn("Input cannot be empty.") }
    }

//    @Test
//    fun `should print error  when state input is invalid`() {
//        // Given
//        every { inputReader.readString() } returnsMany listOf(
//            "Valid Title", "Valid Description", "5", "1", "1"
//        )
//        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
//        coEvery { createTaskUseCase.createTask(any()) } returns validTask
//
//
//        // When
//        createTaskCli.start()
//
//
//        // Then
//        verify {
//            printer.displayLn(match { (it as String).contains("Please enter a valid number") })
//        }
//    }

    @Test
    fun `should print error  when project input is invalid`() {
        // Given
        every { inputReader.readString() } returnsMany listOf(
            "Valid Title", "Valid Description", "4", "-1", "1"
        )
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT, CORRECT_PROJECT)
        coEvery { createTaskUseCase.createTask(any()) } returns validTask


        // When
        createTaskCli.start()


        // Then
        verify {
            printer.displayLn(match { (it as String).contains("Please enter a valid number") })
        }
    }


    @Test
    fun `should print error when EiffelFlowException occurs`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf("valid title", "valid Description", "1", "1")
        coEvery {
            getProjectUseCase.getProjects()
        } returns listOf(CORRECT_PROJECT)
        coEvery {
            createTaskUseCase.createTask(any())
        } throws EiffelFlowException.IOException("Task creation failed")

        createTaskCli.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Task creation failed")
        }
    }

    @Test
    fun `should print generic error when Exception occurs`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf("Task Title", "Task Description", "1", "1")
        coEvery {
            getProjectUseCase.getProjects()
        } returns listOf(CORRECT_PROJECT)
        coEvery {
            createTaskUseCase.createTask(any())
        } throws IOException("Unexpected Error")

        createTaskCli.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Unexpected Error")
        }
    }
}