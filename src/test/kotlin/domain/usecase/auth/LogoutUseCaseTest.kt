package domain.usecase.auth

import io.mockk.*
import org.example.domain.repository.AuthRepository
import org.example.domain.usecase.auth.LogoutUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class LogoutUseCaseTest {
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setup() {
        logoutUseCase = LogoutUseCase(authRepository)
    }

    @Test
    fun `logout should remove user when clearLogin succeed`() {
        every { authRepository.clearLogin() } just runs

        logoutUseCase.logout()

        verify(exactly = 1) { authRepository.clearLogin() }
    }

    @Test
    fun `logout should return failure when clearLogin fails`() {
        val exception = IOException("Failed to clear login")
        every { authRepository.clearLogin() } throws exception

        assertThrows<IOException> { logoutUseCase.logout() }
        verify(exactly = 1) { authRepository.clearLogin() }
    }
}
