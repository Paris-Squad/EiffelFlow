package data.storage.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.data.storage.FileStorageManager
import org.example.data.storage.auth.AuthDataSource
import org.example.data.storage.auth.AuthDataSourceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class AuthDataSourceImplTest {
    private lateinit var authDataSource: AuthDataSource
    private val fileManager: FileStorageManager = mockk()

    @BeforeEach
    fun setUp() {
        authDataSource = AuthDataSourceImpl(fileManager)
    }

    @Test
    fun `saveUserLogin should return success when user ID is saved successfully`() {
        val userId = UUID.randomUUID()

        every { fileManager.writeLinesToFile(userId.toString()) } just runs

        val result = authDataSource.saveUserLogin(userId)

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `saveUserLogin should return failure when an exception occurs`() {
        val userId = UUID.randomUUID()
        val exception = IOException("Failed to write file")

        every { fileManager.writeLinesToFile(any()) } throws exception

        val result = authDataSource.saveUserLogin(userId)

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getIsUserLoggedIn should return true when file has content`() {
        val lines = listOf(UUID.randomUUID().toString())

        every { fileManager.readLinesFromFile() } returns lines

        val result = authDataSource.getIsUserLoggedIn()

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `getIsUserLoggedIn should return false when file has only blank lines`() {
        val lines = listOf("", "  ", "\n")

        every { fileManager.readLinesFromFile() } returns lines

        val result = authDataSource.getIsUserLoggedIn()

        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `getIsUserLoggedIn should return false when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        val result = authDataSource.getIsUserLoggedIn()

        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `getIsUserLoggedIn should return failure when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authDataSource.getIsUserLoggedIn()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `clearLogin should return success when file is cleared successfully`() {
        every { fileManager.clearFile() } just runs

        val result = authDataSource.clearLogin()

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `clearLogin should return failure when an exception occurs`() {
        val exception = FileNotFoundException()
        every { fileManager.clearFile() } throws exception

        val result = authDataSource.clearLogin()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}