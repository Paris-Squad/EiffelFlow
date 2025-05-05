package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.repository.AuthRepositoryImpl
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LoginUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock

class LoginUseCaseTest {
    private val authRepositoryImpl: AuthRepositoryImpl = mockk(relaxed = true)
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setup() {
        loginUseCase = LoginUseCase(authRepositoryImpl)
    }

    @Test
    fun `login should return User when credentials are correct`() {
        val username = UserMock.validUser.username
        val password = UserMock.validUser.password

        every { authRepositoryImpl.loginUser(username, password) } returns UserMock.validUser

        val result = loginUseCase.login(username, password)

        assertThat(result).isEqualTo(UserMock.validUser)
    }

    @Test
    fun `login should threw AuthenticationException when authentication fails`() {
        val username = UserMock.validUser.username
        val password = "wrong password"
        val exception = EiffelFlowException.AuthenticationException(emptySet())

        every { authRepositoryImpl.loginUser(username, password) } throws exception

        assertThrows<EiffelFlowException.AuthenticationException> { loginUseCase.login(username, password) }
    }
}
