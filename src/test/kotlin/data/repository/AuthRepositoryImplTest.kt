package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.data.repository.AuthRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.domain.repository.AuthRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock
import java.io.FileNotFoundException
import java.io.IOException
import java.util.UUID

class AuthRepositoryImplTest {
    private lateinit var authRepository: AuthRepository
    private val fileManager: FileDataSource = mockk()

    @BeforeEach
    fun setUp() {
        authRepository = AuthRepositoryImpl(fileManager)
    }

    @Test
    fun `saveUserLogin should return success when user ID is saved successfully`() {
        try {
            val userId = UserMock.adminUser.userId

            every { fileManager.writeLinesToFile(userId.toString()) } just runs

            val result = authRepository.saveUserLogin(UserMock.adminUser)

            assertThat(result.getOrNull()).isTrue()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("TO handle write")
        }
    }

    @Test
    fun `saveUserLogin should return failure when an exception occurs`() {
        try {
            val exception = IOException("Failed to write file")

            every { fileManager.writeLinesToFile(any()) } throws exception

            val result = authRepository.saveUserLogin(UserMock.adminUser)

            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("TO handle write")
        }
    }

    @Test
    fun `getIsUserLoggedIn should return true when file has content`() {
        try {
            val lines = listOf(UUID.randomUUID().toString())

            every { fileManager.readLinesFromFile() } returns lines

            val result = authRepository.getIsUserLoggedIn()

            assertThat(result.getOrNull()).isTrue()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("TO map the line to user and save it to SessionManager")
        }
    }

    @Test
    fun `getIsUserLoggedIn should return false when file has only blank lines`() {
        try {
            val lines = listOf("", "  ", "\n")

            every { fileManager.readLinesFromFile() } returns lines

            val result = authRepository.getIsUserLoggedIn()

            assertThat(result.getOrNull()).isFalse()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("TO map the line to user and save it to SessionManager")
        }
    }

    @Test
    fun `getIsUserLoggedIn should return false when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        val result = authRepository.getIsUserLoggedIn()

        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `getIsUserLoggedIn should return failure when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authRepository.getIsUserLoggedIn()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `clearLogin should return success when file is cleared successfully`() {
        every { fileManager.clearFile() } just runs

        val result = authRepository.clearLogin()

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `clearLogin should return failure when an exception occurs`() {
        val exception = FileNotFoundException()
        every { fileManager.clearFile() } throws exception

        val result = authRepository.clearLogin()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}