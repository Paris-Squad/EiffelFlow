package presentation.presenter

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.presentation.presenter.RegisterPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class RegisterPresenterTest {
    private val registerUseCase: RegisterUseCase = mockk(relaxed = true)
    private lateinit var registerPresenter: RegisterPresenter
    private val user = UserMock

    @BeforeEach
    fun setup() {
        registerPresenter = RegisterPresenter(registerUseCase)

    }

    @Test
    fun `should return user when registration is successful`() {

        coEvery { registerUseCase.register(
            user.validUser.username,
            user.validUser.password,
            user.validUser.role)
        }returns user.validUser

        val result = registerPresenter.register(user.validUser.username, user.validUser.password, user.validUser.role)

        assertThat(result).isEqualTo(user.validUser)

    }

    @Test
    fun `should throw exception when registration fails`() {
        val exception = EiffelFlowException.AuthorizationException("Not allowed")
        coEvery {
            registerUseCase.register(user.validUser.username, user.validUser.password, user.validUser.role)
        } throws exception

        val thrown = assertThrows<EiffelFlowException.AuthorizationException> {
            registerPresenter.register(user.validUser.username, user.validUser.password, user.validUser.role)
        }
        assertThat(thrown.message).contains("Not allowed")
    }

    @Test
    fun `should throw Exception when an unexpected exception occurs during registration`() {
        // Given
        val exception = Exception("Unexpected error")

        coEvery {
            registerUseCase.register(user.validUser.username, user.validUser.password, user.validUser.role)
        } throws exception

        // When / Then
        val thrown = assertThrows<RuntimeException> {
            registerPresenter.register(user.validUser.username, user.validUser.password, user.validUser.role)
        }
        assertThat(thrown.message).contains("An error occurred during registration: Unexpected error")
    }
}