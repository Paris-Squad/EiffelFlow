package presentation.user

import io.mockk.*
import kotlinx.coroutines.delay
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.user.UpdateUserUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.user.UpdateUserCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class UpdateUserCLITest {

    private val updateUserUseCase: UpdateUserUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private lateinit var updateUserCLI: UpdateUserCLI

    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        updateUserCLI = UpdateUserCLI(
            updateUserUseCase = updateUserUseCase,
            inputReader = inputReader,
            printer = printer
        )
    }

    @Test
    fun `should update User successfully when input is valid`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            UserMock.validUser.password,
            UserMock.updateUser.password
        )
        coEvery {
            updateUserUseCase.updateUser(any(), any(), any())
        } returns UserMock.updateUser

        // When
        updateUserCLI.start()

        // Then
        verify { printer.displayLn("User updated successfully ${UserMock.updateUser}") }
    }

    @Test
    fun `should print error when user name is empty`() {
        // Given
        every { inputReader.readString() } returns ""

        // When
        updateUserCLI.start()

        // Then
        verify { printer.displayLn("User name cannot be empty or null.") }
    }

    @Test
    fun `should print error when user name is null`() {
        // Given
        every { inputReader.readString() } returns null

        // When
        updateUserCLI.start()

        // Then
        verify(exactly = 1) {
            printer.displayLn("User name cannot be empty or null.")
        }
    }

    @Test
    fun `should print error when current password is empty`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            "",
            UserMock.updateUser.password
        )

        // When
        updateUserCLI.start()

        // Then
        verify { printer.displayLn("Password cannot be empty or null.") }
    }

    @Test
    fun `should print error when current password is null`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            null,
            UserMock.updateUser.password
        )

        // When
        updateUserCLI.start()

        // Then
        verify(exactly = 1) {
            printer.displayLn("Password cannot be empty or null.")
        }
    }

    @Test
    fun `should print error when new password is empty`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            UserMock.validUser.password,
            ""
        )

        // When
        updateUserCLI.start()

        // Then
        verify { printer.displayLn("Password cannot be empty or null.") }
    }

    @Test
    fun `should print error when new password is null`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            UserMock.validUser.password,
            null
        )

        // When
        updateUserCLI.start()

        // Then
        verify(exactly = 1) {
            printer.displayLn("Password cannot be empty or null.")
        }
    }

    @Test
    fun `should throw Exception when update User fail`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            UserMock.validUser.password,
            UserMock.validUser.password
        )
        coEvery {
            updateUserUseCase.updateUser(any(), any(), any())
        } throws EiffelFlowException.AuthorizationException("Current password is not correct")

        //When
        updateUserCLI.start()

        //Then
        verify { printer.displayLn("Authorization failed:Current password is not correct") }
    }

    @Test
    fun `should handle multiple suspension points in updateUserUseCase`() {
        // Given
        every {
            inputReader.readString()
        } returnsMany listOf(
            UserMock.updateUser.username,
            UserMock.validUser.password,
            UserMock.updateUser.password
        )

        coEvery {
            updateUserUseCase.updateUser(any(), any(), any())
        } coAnswers {
            delay(1000)
            UserMock.validUser
        }

        // When
        updateUserCLI.start()

        // Then
        coVerify {
            updateUserUseCase.updateUser(any(), any(), any())
        }
        verify {
            printer.displayLn("User updated successfully ${UserMock.validUser}")
        }
    }
}