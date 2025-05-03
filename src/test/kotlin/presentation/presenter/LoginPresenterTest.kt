package presentation.presenter

import com.google.common.truth.Truth.assertThat
import io.mockk.every
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
    fun `should return success when login is successful`(){
        try {
            every { loginUseCase.login(user.validUser.username,user.validUser.password
            ) }returns Result.success("Login successful")

            val result= loginPresenter.onLoginClicked(user.validUser.username,user.validUser.password)

            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo("Login successful")

        }catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }
    @Test
    fun `should return failure when login fails`(){
        try {
            val exception = EiffelFlowException.AuthorizationException("Invalid username or password")

            every { loginUseCase.login(user.validUser.username,user.validUser.password
            ) }returns Result.failure(exception)

            val result= loginPresenter.onLoginClicked(user.validUser.username,user.validUser.password)

            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(exception)

        }catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }

}