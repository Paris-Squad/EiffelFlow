package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.data.repository.AuthRepositoryImpl
import org.example.data.storage.auth.AuthDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.UUID

class AuthRepositoryImplTest {
    private val authDataSource: AuthDataSource = mockk(relaxed = true)
    private lateinit var authRepository: AuthRepositoryImpl

    @BeforeEach
    fun setUp() {
        authRepository = AuthRepositoryImpl(authDataSource)
    }

    @Test
    fun `saveUserLogin should return success when data source succeeds`() {
        val userId = UUID.randomUUID()

        every { authDataSource.saveUserLogin(userId) } returns Result.success(true)

        val result = authRepository.saveUserLogin(userId)

        assertThat(result.getOrNull()).isEqualTo(true)
        verify(exactly = 1) { authDataSource.saveUserLogin(userId) }
    }

    @Test
    fun `saveUserLogin should return failure when data source fails`() {
        val userId = UUID.randomUUID()
        val exception = IOException()

        every { authDataSource.saveUserLogin(userId) } returns Result.failure(exception)

        val result = authRepository.saveUserLogin(userId)

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        verify(exactly = 1) { authDataSource.saveUserLogin(userId) }
    }

    @Test
    fun `getIsUserLoggedIn should return logged in status when data source succeeds`() {
        every { authDataSource.getIsUserLoggedIn() } returns Result.success(true)

        val result = authRepository.getIsUserLoggedIn()

        assertThat(result.getOrNull()).isTrue()
        verify(exactly = 1) { authDataSource.getIsUserLoggedIn() }
    }

    @Test
    fun `getIsUserLoggedIn should return failure when data source fails`() {
        val exception = IOException()

        every { authDataSource.getIsUserLoggedIn() } returns Result.failure(exception)

        val result = authRepository.getIsUserLoggedIn()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        verify(exactly = 1) { authDataSource.getIsUserLoggedIn() }
    }

    @Test
    fun `clearLogin should return success when data source succeeds`() {
        every { authDataSource.clearLogin() } returns Result.success(true)

        val result = authRepository.clearLogin()

        assertThat(result.getOrNull()).isTrue()
        verify(exactly = 1) { authDataSource.clearLogin() }
    }

    @Test
    fun `clearLogin should return failure when data source fails`() {
        val exception = IOException()

        every { authDataSource.clearLogin() } returns Result.failure(exception)

        val result = authRepository.clearLogin()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        verify(exactly = 1) { authDataSource.clearLogin() }
    }
}