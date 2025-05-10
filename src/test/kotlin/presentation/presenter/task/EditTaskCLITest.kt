package presentation.presenter.task


import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.EditTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.task.EditTaskCli
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock.CORRECT_PROJECT
import utils.TaskMock.validTask
import utils.UserMock
import java.io.IOException

class EditTaskCLITest {

    private val editTaskUseCase: EditTaskUseCase = mockk()
    private lateinit var editTaskCli: EditTaskCli
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private val getProjectUseCase: GetProjectUseCase = mockk()


    @BeforeEach
    fun setup() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        editTaskCli =
            EditTaskCli(
                editTaskUseCase = editTaskUseCase,
                inputReader = inputReader,
                printer = printer,
                getProjectUseCase = getProjectUseCase
            )
    }

    @Test
    fun `should return the edit task when task is successfully edited`() {
        //Given
        every { inputReader.readString() } returnsMany listOf("valid title", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask

        //When
        val result = editTaskCli.start()

        // Then
        verify {
            printer.displayLn(match { (it as String).contains("Task updated successfully") })
        }
    }


    @Test
    fun `should print error when task name is empty`() {
        //Given
        every { inputReader.readString() } returnsMany listOf("", "valid title", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask
        //When
        editTaskCli.start()
        // Then
        verify { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when task name is null`() {
        //Given
        every { inputReader.readString() } returnsMany listOf(null, "valid title", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask
        //When
        editTaskCli.start()
        // Then
        verify { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when description is empty`() {
        //Given
        every { inputReader.readString() } returnsMany listOf("valid title", "", "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask
        //When
        editTaskCli.start()
        // Then
        verify { printer.displayLn("Input cannot be empty.") }
    }

    @Test
    fun `should print error when description is null`() {
        //Given
        every { inputReader.readString() } returnsMany listOf("valid title", null, "Valid Description", "1", "1")
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask
        //When
        editTaskCli.start()
        // Then
        verify { printer.displayLn("Input cannot be empty.") }
    }


    @Test
    fun `should print error when user select project not found`() {
        runTest {
            //Given
            val projects = listOf(CORRECT_PROJECT)
            every {
                inputReader.readString()
            } returnsMany listOf("valid title", "Valid Description", "1", null, "1")
            coEvery { getProjectUseCase.getProjects() } returns projects
            coEvery { editTaskUseCase.editTask(any()) } returns validTask

            //When
            editTaskCli.start()

            // Then
            verify {
                printer.displayLn("Please enter a valid number between 1 and ${projects.size}")
            }

        }
    }

    @Test
    fun `should print IOException when getting projects failed`() {
        runTest {
            //Given
            val exception = EiffelFlowException.IOException("Something went wrong:An error occurred: Unexpected Error")
            every {
                inputReader.readString()
            } returnsMany listOf("valid title", "Valid Description", "1", "1")
            coEvery {
                getProjectUseCase.getProjects()
            } throws exception
            //When
            editTaskCli.start()

            // Then
            verify {
                printer.displayLn(match { (it as String).contains("Something went wrong:An error occurred: Unexpected Error") })
            }

        }
    }
    @Test
    fun `should print NotFoundException when getting projects failed`() {
        runTest {
            //Given
            val exception = EiffelFlowException.NotFoundException("An error occurred: Unexpected Error")
            every {
                inputReader.readString()
            } returnsMany listOf("valid title", "Valid Description", "1", "1")
            coEvery {
                getProjectUseCase.getProjects()
            } throws exception
            //When
            editTaskCli.start()

            // Then
            verify {
                printer.displayLn("An error occurred: Unexpected Error")
            }

        }
    }

    @Test
    fun `should not update task when projects are empty`() {
        runTest {
            //Given
            every { inputReader.readString() } returnsMany listOf("valid title", "Valid Description", "1", "1")
            coEvery { getProjectUseCase.getProjects() } returns listOf()
            coEvery { editTaskUseCase.editTask(any()) } returns validTask

            //When
            editTaskCli.start()

            // Then
            verify {
                printer.displayLn("Enter task title:")
            }

        }
    }

    @Test
    fun `should print error  when project input is invalid`() {
        // Given
        every { inputReader.readString() } returnsMany listOf(
            "Valid Title", "Valid Description", "4", "-1", "1"
        )
        coEvery { getProjectUseCase.getProjects() } returns listOf(CORRECT_PROJECT, CORRECT_PROJECT)
        coEvery { editTaskUseCase.editTask(any()) } returns validTask


        // When
        editTaskCli.start()


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
            editTaskUseCase.editTask(any())
        } throws EiffelFlowException.IOException("Task creation failed")

        editTaskCli.start()

        // Then
        verify {
            printer.displayLn("Something went wrong:Task creation failed")
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
            editTaskUseCase.editTask(any())
        } throws IOException("Unexpected Error")

        editTaskCli.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Unexpected Error")
        }
    }

}




