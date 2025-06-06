package presentation.presenter.auth

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.usecase.user.CreateUserUseCase
import org.example.presentation.user.CreateUserCLI
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterCLITest {
    private val registerUseCase: CreateUserUseCase = mockk(relaxed = true)
    private lateinit var registerCli: CreateUserCLI
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        registerCli = CreateUserCLI(createUserUseCase =registerUseCase, inputReader = inputReader, printer = printer)

    }

    @Test
    fun `should print success message when valid input entered`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "2")
        coEvery { registerUseCase.register("user", "password123", RoleType.ADMIN) } returns User(
            username = "user",
            password = "password123",
            role = RoleType.ADMIN
        )

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Registration successful")
        }
    }

    @Test
    fun `should display error when username is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("", "password123")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("user name cannot be empty.")
        }
    }

    @Test
    fun `should display error when username is null`() {
        // Given
        every { inputReader.readString() } returns null

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("user name cannot be empty.")
        }
    }

    @Test
    fun `should display error when password is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("password cannot be empty.")
        }
    }

    @Test
    fun `should display error when password is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", null)

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("password cannot be empty.")
        }
    }

    @Test
    fun `should display error message when role selection is out of range`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "5")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Invalid role selection.")
        }
    }

    @Test
    fun `should display error message when role input is non-numeric`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "invalid")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Invalid role selection.")
        }
    }

    @Test
    fun `should display error message when role input is empty`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Invalid role selection.")
        }
    }

    @Test
    fun `should display error message when role input is null`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", null)

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Invalid role selection.")
        }
    }

    @Test
    fun `should display error message when exception occurs`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "1")
        coEvery { registerUseCase.register("user", "password123", RoleType.MATE) } throws RuntimeException("Unexpected error")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("An error occurred: Unexpected error")
        }
    }

    @Test
    fun `should display Register Failed when AuthorizationException is thrown`() {
        // Given
        every { inputReader.readString() } returnsMany listOf("user", "password123", "1")
        coEvery {
            registerUseCase.register("user", "password123", RoleType.MATE)
        } throws EiffelFlowException.AuthorizationException("Unauthorized")

        // When
        registerCli.start()

        // Then
        verify {
            printer.displayLn("Authorization failed:Unauthorized")
        }
    }

}