package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.repository.AuthRepository
import org.example.domain.usecase.auth.LogoutUseCase
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class LogoutUseCaseTest {
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setup() {
        logoutUseCase = LogoutUseCase(authRepository)
    }

    @Test
    fun `logout should return success when clearLogin succeeds`() {
        every { authRepository.clearLogin() } returns Result.success(true)

        val result = logoutUseCase.logout()

        assertTrue(result.isSuccess)
        verify { authRepository.clearLogin() }
    }

    @Test
    fun `logout should return failure when clearLogin fails`() {
        val exception = IOException("Failed to clear login")
        every { authRepository.clearLogin() } returns Result.failure(exception)

        val result = logoutUseCase.logout()

        assertThat(result.exceptionOrNull()).isInstanceOf(exception::class.java)
        verify { authRepository.clearLogin() }
    }
}