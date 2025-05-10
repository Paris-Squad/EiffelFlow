package presentation.presenter.auth

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase
import org.example.presentation.auth.LoginCLI
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class LoginCLITest {
    private val loginUseCase: LoginUseCase = mockk(relaxed = true)
    private lateinit var loginPresenter: LoginCLI
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)
    private val user = UserMock

    @BeforeEach
    fun setup() {
        loginPresenter = LoginCLI(loginUseCase = loginUseCase, inputReader = inputReader, printer = printer)
    }

    @Test
    fun `should print success message when login is successful`() {
        every { inputReader.readString() } returnsMany listOf(user.validUser.username, user.validUser.password)
        coEvery {
            loginUseCase.login(user.validUser.username, user.validUser.password)
        } returns user.validUser

         loginPresenter.start()

        verify { printer.displayLn("Login successful") }
    }

    @Test
    fun `should print error message when login fails`() {
        every { inputReader.readString() } returnsMany listOf(user.validUser.username, user.validUser.password)
        coEvery { loginUseCase.login(user.validUser.username, user.validUser.password) } throws EiffelFlowException.AuthorizationException("Login Failed")

        loginPresenter.start()

        verify { printer.displayLn("Authorization failed:Login Failed") }
    }


    @Test
    fun `should print error message when username is blank`() {
        every { inputReader.readString() } returnsMany listOf("", "anyPassword")

        loginPresenter.start()

        verify { printer.displayLn("user name cannot be empty.") }
    }

    @Test
    fun `should print error message when username is null`() {
        every { inputReader.readString() } returns null

        loginPresenter.start()

        verify { printer.displayLn("user name cannot be empty.") }
    }

    @Test
    fun `should print error message when password is blank`() {
        every { inputReader.readString() } returnsMany listOf("name", "")

        loginPresenter.start()

        verify { printer.displayLn("password cannot be empty.") }
    }

    @Test
    fun `should print error when password is empty`() {
        every { inputReader.readString() } returnsMany listOf("validUser", null)

        loginPresenter.start()

        verify { printer.displayLn("password cannot be empty.") }
    }


}