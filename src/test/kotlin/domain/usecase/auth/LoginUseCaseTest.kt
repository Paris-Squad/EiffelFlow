package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.repository.AuthRepositoryImpl
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class LoginUseCaseTest {
    private val authRepositoryImpl: AuthRepositoryImpl = mockk(relaxed = true)
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setup() {
        loginUseCase = LoginUseCase(authRepositoryImpl)
    }

    @Test
    fun `login should return success when credentials are correct`() {
        val username = UserMock.validUser.username
        val password = UserMock.validUser.password

        every { authRepositoryImpl.loginUser(username, password) } returns Result.success("Login successfully")

        val result = loginUseCase.login(username, password)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `login should return success message when credentials are correct`() {
        val username = UserMock.validUser.username
        val password = UserMock.validUser.password

        every { authRepositoryImpl.loginUser(username, password) } returns Result.success("Login successfully")

        val result = loginUseCase.login(username, password)

        assertThat(result.getOrNull()).isEqualTo("Login successfully")
    }

    @Test
    fun `login should return failure when authentication fails`() {
        val username = UserMock.validUser.username
        val password = "wrong password"
        val exception = EiffelFlowException.AuthenticationException(emptySet())

        every { authRepositoryImpl.loginUser(username, password) } returns Result.failure(exception)

        val result = loginUseCase.login(username, password)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `login should return authentication exception when authentication fails`() {
        val username = UserMock.validUser.username
        val password = "wrong password"
        val exception = EiffelFlowException.AuthenticationException(emptySet())

        every { authRepositoryImpl.loginUser(username, password) } returns Result.failure(exception)

        val result = loginUseCase.login(username, password)

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `login should return failure when saving login fails`() {
        val username = UserMock.validUser.username
        val password = UserMock.validUser.password
        val exception = RuntimeException("Failed to save login")

        every { authRepositoryImpl.loginUser(username, password) } returns Result.failure(exception)

        val result = loginUseCase.login(username, password)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `login should return save exception when saving login fails`() {
        val username = UserMock.validUser.username
        val password = UserMock.validUser.password
        val exception = RuntimeException("Failed to save login")

        every { authRepositoryImpl.loginUser(username, password) } returns Result.failure(exception)

        val result = loginUseCase.login(username, password)

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
