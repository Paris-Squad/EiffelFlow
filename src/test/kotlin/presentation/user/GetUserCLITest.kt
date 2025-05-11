package presentation.user

import io.mockk.mockk
import org.example.domain.usecase.user.GetUserUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.user.GetUserCLI
import org.junit.jupiter.api.BeforeEach
import io.mockk.*
import org.junit.jupiter.api.Test
import utils.UserMock

class GetUserCLITest {

    private val getUserUseCase: GetUserUseCase = mockk(relaxed = true)
    private lateinit var getUserCLI: GetUserCLI
    private val printer: Printer = mockk(relaxed = true)
    private val inputReader: InputReader = mockk()

    @BeforeEach
    fun setup() {
        getUserCLI = GetUserCLI(getUserUseCase = getUserUseCase, printer = printer, inputReader = inputReader)
    }

    // region get users
    @Test
    fun `should display list of users when users are available`() {
        // Given
        coEvery { getUserUseCase.getUsers() } returns listOf(UserMock.validUser)

        // When
        getUserCLI.viewAllUsers()

        // Then
        verify {
            printer.displayLn("1. ${UserMock.validUser.userId} - ${UserMock.validUser.username}")
        }
    }

    @Test
    fun `should display no users found when list is empty`() {
        // Given
        coEvery { getUserUseCase.getUsers() } returns emptyList()

        // When
        getUserCLI.viewAllUsers()

        // Then
        verify { printer.displayLn("No users found.") }
    }

    @Test
    fun `should display error when unexpected exception occurs during user retrieval`() {
        // Given
        coEvery { getUserUseCase.getUsers() } throws RuntimeException("Unexpected")

        // When
        getUserCLI.viewAllUsers()

        // Then
        verify { printer.displayLn("An error occurred: Unexpected") }
    }
    // end region


    // region displayUserById
    @Test
    fun `should display user details when valid userId is entered`() {
        // Given
        every { inputReader.readString() } returns UserMock.validUser.userId.toString()
        coEvery { getUserUseCase.getUserById(UserMock.validUser.userId) } returns UserMock.validUser

        // When
        getUserCLI.displayUserById()

        // Then
        verifySequence {
            printer.displayLn("Enter User ID : ")
            printer.displayLn("user details : ${UserMock.validUser.userId} - ${UserMock.validUser.username} - ${UserMock.validUser.role} ")
        }
    }

    @Test
    fun `should display error when input is blank`() {
        // Given
        every { inputReader.readString() } returns "  "

        // When
        getUserCLI.displayUserById()

        // Then
        verifySequence {
            printer.displayLn("Enter User ID : ")
            printer.displayLn("user ID cannot be empty.")
        }
    }

    @Test
    fun `should display error when input is null`() {
        // Given
        every { inputReader.readString() } returns null

        // When
        getUserCLI.displayUserById()

        // Then
        verifySequence {
            printer.displayLn("Enter User ID : ")
            printer.displayLn("user ID cannot be empty.")
        }
    }

    // endregion

}