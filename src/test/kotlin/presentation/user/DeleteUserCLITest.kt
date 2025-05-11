package presentation.user

import io.mockk.*
import kotlinx.coroutines.delay
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.user.DeleteUserUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.user.DeleteUserCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class DeleteUserCLITest {

    private val deleteUserUseCase: DeleteUserUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var deleteUserCLI: DeleteUserCLI

    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        deleteUserCLI = DeleteUserCLI(
            deleteUserUseCase = deleteUserUseCase,
            inputReader = inputReader,
            printer = printer
        )
    }

    @Test
    fun `should deleted User successfully when input is valid`() {
        // Given
        every {
            inputReader.readString()
        } returns UserMock.userToDelete.userId.toString()
        coEvery {
            deleteUserUseCase.deleteUser(any())
        } returns UserMock.userToDelete

        // When
        deleteUserCLI.start()

        // Then
//        coVerify(exactly = 1) { deleteUserUseCase.deleteUser(UserMock.userToDelete.userId) }
        verify { printer.displayLn("User deleted successfully ${UserMock.userToDelete}") }
    }

    @Test
    fun `should print error when userId is empty`() {
        // Given
        every { inputReader.readString() } returns ""

        // When
        deleteUserCLI.start()

        // Then
        verify { printer.displayLn("User ID cannot be empty.") }
    }

    @Test
    fun `should print error when userId is null`() {
        // Given
        every { inputReader.readString() } returns null

        // When
        deleteUserCLI.start()

        // Then
        verify(exactly = 1) {
            printer.displayLn("User ID cannot be empty.")
        }
    }

    @Test
    fun `should print error when input is not a valid UUID`() {
        //Given
        every {
            inputReader.readString()
        } returns "UserMock.userToDelete.userId"
        every { printer.displayLn(any()) } just Runs

        //When
        deleteUserCLI.start()

        //Then
        verify { printer.displayLn("An error occurred: Invalid UUID string: UserMock.userToDelete.userId") }
    }

    @Test
    fun `should throw Exception when delete User fail`() {
        // Given
        coEvery {
            deleteUserUseCase.deleteUser(UserMock.userToDelete.userId)
        } throws EiffelFlowException.IOException("User not found")

        //When
        deleteUserCLI.start()

        //Then
        verify { printer.displayLn(match { (it as String).contains("An error occurred:") }) }
    }

    @Test
    fun `should handle multiple suspension points in deleteUserUseCase`() {
        // Given
        every {
            inputReader.readString()
        } returns UserMock.userToDelete.userId.toString()

        coEvery {
            deleteUserUseCase.deleteUser(any())
        } coAnswers {
            delay(1000)
            UserMock.userToDelete
        }

        // When
        deleteUserCLI.start()

        // Then
        coVerify { deleteUserUseCase.deleteUser(any()) }
        verify {
            printer.displayLn("User deleted successfully ${UserMock.userToDelete}")
        }
    }
}