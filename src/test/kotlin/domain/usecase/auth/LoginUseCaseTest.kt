package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
        runTest {
            // Given
            val username = UserMock.validUser.username
            val password = UserMock.validUser.password

            coEvery { authRepositoryImpl.loginUser(username, password) } returns UserMock.validUser

            // When
            val result = loginUseCase.login(username, password)

            // Then
            assertThat(result).isEqualTo(UserMock.validUser)
        }
    }

    @Test
    fun `login should threw AuthenticationException when authentication fails`() {
        runTest {
            // Given
            val username = UserMock.validUser.username
            val password = "wrong password"
            val exception = EiffelFlowException.AuthenticationException(emptySet())

            coEvery { authRepositoryImpl.loginUser(username, password) } throws exception

            // When / Then
            assertThrows<EiffelFlowException.AuthenticationException> { loginUseCase.login(username, password) }
        }
    }
}


