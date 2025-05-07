package presentation.presenter

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase
import org.example.presentation.presenter.LoginPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class LoginPresenterTest {
    private val loginUseCase: LoginUseCase = mockk(relaxed = true)
    private lateinit var loginPresenter: LoginPresenter
    private val user = UserMock

    @BeforeEach
    fun setup() {
        loginPresenter = LoginPresenter(loginUseCase)
    }

    @Test
    fun `should return success when login is successful`() {
        coEvery {
            loginUseCase.login(user.validUser.username, user.validUser.password)
        } returns user.validUser

        val result = loginPresenter.onLoginClicked(user.validUser.username, user.validUser.password)

        assertThat(result).isEqualTo("Login successful")
    }

    @Test
    fun `should return default login failed message when exception message is null`() {
        // Given
        val exception = EiffelFlowException.AuthorizationException(null)
        coEvery {
            loginUseCase.login(user.validUser.username, user.validUser.password)
        } throws exception

        // When
        val result = loginPresenter.onLoginClicked(user.validUser.username, user.validUser.password)

        // Then
        assertThat(result).isEqualTo("Login failed")
    }

    @Test
    fun `should return exception message when login fails with message`() {
        // Given
        val exception = EiffelFlowException.AuthorizationException("Invalid credentials")
        coEvery {
            loginUseCase.login(user.validUser.username, user.validUser.password)
        } throws exception

        // When
        val result = loginPresenter.onLoginClicked(user.validUser.username, user.validUser.password)

        // Then
        assertThat(result).isEqualTo("Invalid credentials")
    }

}

