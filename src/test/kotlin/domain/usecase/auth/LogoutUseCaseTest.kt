package domain.usecase.auth

import io.mockk.*
import kotlinx.coroutines.test.runTest
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
        runTest {
            // Given
            coEvery { authRepository.clearLogin() } just runs

            logoutUseCase.logout()

            // then
            coVerify(exactly = 1) { authRepository.clearLogin() }
        }
    }

    @Test
    fun `logout should return failure when clearLogin fails`() {
        runTest {
            // Given
            val exception = IOException("Failed to clear login")
            coEvery { authRepository.clearLogin() } throws exception

            // When / Then
            assertThrows<IOException> { logoutUseCase.logout() }
            coVerify(exactly = 1) { authRepository.clearLogin() }
        }
    }
}