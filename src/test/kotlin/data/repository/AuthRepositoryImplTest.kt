package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.repository.AuthRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuthRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.UserMock
import utils.UserMock.validUser
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.test.assertEquals

class AuthRepositoryImplTest {
    private lateinit var authRepository: AuthRepository
    private val authFileManager: FileDataSource = mockk()
    private val userFileManager: FileDataSource = mockk()
    private val userMapper: UserCsvParser = mockk()

    @BeforeEach
    fun setUp() {
        authRepository = AuthRepositoryImpl(
            authFileDataSource = authFileManager,
            usersFileDataSource = userFileManager,
            userCsvParser = userMapper
        )
    }

    @AfterEach
    fun tearDown() {
        SessionManger.logout()
    }


    @Test
    fun `saveUserLogin should return User when user is saved successfully`() {
        runTest {
            // Given
            val user = UserMock.adminUser
            val userCsv = "user-csv-string"

            every { userMapper.serialize(user) } returns userCsv
            every { authFileManager.writeLinesToFile(userCsv) } just runs

            // When
            val result = authRepository.saveUserLogin(user)

            // Then
            assertThat(result).isEqualTo(user)
        }
    }

    @Test
    fun `saveUserLogin should store user in session manager`() {
        runTest {
            // Given
            val user = UserMock.adminUser
            val userCsv = "user-csv-string"

            every { userMapper.serialize(user) } returns userCsv
            every { authFileManager.writeLinesToFile(userCsv) } just runs

            // When
            authRepository.saveUserLogin(user)

            // Then
            assertThat(SessionManger.getUser()).isEqualTo(user)
        }
    }

    @Test
    fun `saveUserLogin should write to file`() {
        runTest {
            // Given
            val user = UserMock.adminUser
            val userCsv = "user-csv-string"

            every { userMapper.serialize(user) } returns userCsv
            every { authFileManager.writeLinesToFile(userCsv) } just runs

            // When
            authRepository.saveUserLogin(user)

            // Then
            verify { authFileManager.writeLinesToFile(userCsv) }
        }
    }

    @Throws
    @Test
    fun `saveUserLogin should threw IOException when an exception occurs`() {
        runTest {
            // Given
            val user = UserMock.adminUser
            val userCsv = "user-csv-string"
            val exception = IOException("Failed to write file")

            every { userMapper.serialize(user) } returns userCsv
            every { authFileManager.writeLinesToFile(userCsv) } throws exception

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.saveUserLogin(user)
            }
        }
    }

    @Test
    fun `saveUserLogin should return exception when an exception occurs`() {
        runTest {
            // Given
            val user = UserMock.adminUser
            val userCsv = "user-csv-string"
            val exception = IOException("Failed to write file")

            every { userMapper.serialize(user) } returns userCsv
            every { authFileManager.writeLinesToFile(userCsv) } throws exception

            // When /Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.saveUserLogin(user)
            }
        }
    }

    @Test
    fun `isUserLoggedIn should return true when file has content`() {
        runTest {
            // Given
            val userCsv = "user-csv-string"
            val lines = listOf(userCsv)
            val user = UserMock.adminUser

            every { authFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(userCsv) } returns user

            // When /Then
            val result = authRepository.isUserLoggedIn()

            // Then
            assertThat(result).isTrue()
        }
    }

    @Test
    fun `isUserLoggedIn should set user in session manager when file has content`() {
        runTest {
            // Given
            val userCsv = "user-csv-string"
            val lines = listOf(userCsv)
            val user = UserMock.adminUser

            every { authFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(userCsv) } returns user

            // When
            authRepository.isUserLoggedIn()

            // Then
            assertThat(SessionManger.isLoggedIn()).isTrue()
            assertThat(SessionManger.getUser()).isEqualTo(user)
        }
    }

    @Test
    fun `isUserLoggedIn should return false when file has only blank lines`() {
        runTest {
            // Given
            val lines = listOf("", "  ", "\n")
            every { authFileManager.readLinesFromFile() } returns lines

            // When
            val result = authRepository.isUserLoggedIn()

            // Then
            assertThat(result).isFalse()
        }
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file has only blank lines`() {
        runTest {
            // Given
            val lines = listOf("", "  ", "\n")
            every { authFileManager.readLinesFromFile() } returns lines

            // When
            authRepository.isUserLoggedIn()

            // Then
            assertThat(SessionManger.isLoggedIn()).isFalse()
        }
    }

    @Test
    fun `isUserLoggedIn should return false when file not found`() {
        runTest {
            // Given
            every { authFileManager.readLinesFromFile() } throws FileNotFoundException()
            // When
            val result = authRepository.isUserLoggedIn()

            // Then
            assertThat(result).isFalse()
        }
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file not found`() {
        runTest {
            // Given
            every { authFileManager.readLinesFromFile() } throws FileNotFoundException()

            // When
            authRepository.isUserLoggedIn()

            // Then
            assertThat(SessionManger.isLoggedIn()).isFalse()
        }
    }

    @Test
    fun `isUserLoggedIn should threw IOException when other exception occurs`() {
        runTest {
            // Given
            val exception = EiffelFlowException.IOException("Failed to read file")
            every { authFileManager.readLinesFromFile() } throws exception

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.isUserLoggedIn()
            }
        }
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when other exception occurs`() {
        runTest {
            // Given
            val exception = IOException("Failed to read file")
            every { authFileManager.readLinesFromFile() } throws exception

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.isUserLoggedIn()
            }
        }
    }

    @Test
    fun `clearLogin should clear login data when file is cleared successfully`() {
        runTest {
            // Given
            SessionManger.login(UserMock.adminUser)
            every { authFileManager.clearFile() } just runs
            authRepository.clearLogin()

            // When / Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                SessionManger.getUser()
            }
        }
    }

    @Test
    fun `clearLogin should logout user from session manager`() {
        runTest {
            // Given
            SessionManger.login(UserMock.adminUser)
            every { authFileManager.clearFile() } just runs

            // When
            authRepository.clearLogin()

            // Then
            assertThat(SessionManger.isLoggedIn()).isFalse()
        }
    }

    @Test
    fun `clearLogin should return failure when an exception occurs`() {
        runTest {
            // Given
            val exception = FileNotFoundException()

            SessionManger.login(UserMock.adminUser)
            every { authFileManager.clearFile() } throws exception

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.clearLogin()
            }
        }
    }

    @Test
    fun `loginUser should return success when username and password are correct`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "validPass"
            val user = validUser
            val lines = listOf("user-csv-string")

            every { userFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(any()) } returns user
            every { userMapper.serialize(user) } returns "user-csv-string"
            every { authFileManager.writeLinesToFile(any()) } just runs

            // When
            val result = authRepository.loginUser(username, password)

            // Then
            assertEquals(user, result)
        }
    }

    @Test
    fun `loginUser should save user login when username and password are correct`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "validPass"
            val user = validUser
            val lines = listOf("user-csv-string")

            every { userFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(any()) } returns user
            every { userMapper.serialize(user) } returns "user-csv-string"
            every { authFileManager.writeLinesToFile(any()) } just runs

            // when
            authRepository.loginUser(username, password)

            // Then
            assertThat(SessionManger.isLoggedIn()).isTrue()
        }
    }

    @Test
    fun `loginUser should return AuthenticationException when username is not found`() {
        runTest {
            // Given
            val username = "invalidUser"
            val password = "validPass"
            val user = validUser
            val lines = listOf("user-csv-string")

            every { userFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(any()) } returns user

            // When / Then
            assertThrows<EiffelFlowException.AuthenticationException> {
                authRepository.loginUser(username, password)
            }
        }
    }

    @Test
    fun `loginUser should return AuthenticationException when password is incorrect`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "wrongPass"
            val user = validUser
            val lines = listOf("user-csv-string")

            every { userFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(any()) } returns user

            // When / Then
            assertThrows<EiffelFlowException.AuthenticationException> {
                authRepository.loginUser(username, password)
            }
        }
    }

    @Test
    fun `loginUser should return IOException when file operation fails`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "validPass"
            val exception = IOException("Failed to read file")

            every { userFileManager.readLinesFromFile() } throws exception

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                authRepository.loginUser(username, password)
            }
        }
    }

    @Test
    fun `loginUser should filter out blank lines`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "validPass"
            val user = validUser
            val lines = listOf("", "  ", "user-csv-string", "\n")

            every { userFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine("user-csv-string") } returns user
            every { userMapper.serialize(user) } returns "user-csv-string"
            every { authFileManager.writeLinesToFile(any()) } just runs

            // When
            val result = authRepository.loginUser(username, password)

            // Then
            assertThat(result).isEqualTo(user)
        }
    }

    @Test
    fun `loginUser should throw EiffelFlowException directly when it occurs`() {
        runTest {
            // Given
            val username = "validUser"
            val password = "validPass"
            val eiffelException = EiffelFlowException.AuthorizationException("Authorization failed")

            every { userFileManager.readLinesFromFile() } throws eiffelException

            // When / Then
            assertThrows<EiffelFlowException.AuthorizationException> {
                authRepository.loginUser(username, password)
            }
        }
    }

    @Test
    fun `isUserLoggedIn should use first non-blank line when mixed with blank lines`() {
        runTest {
            // Given
            val userCsv = "user-csv-string"
            val lines = listOf("", "  ", userCsv, "another-line")
            val user = UserMock.adminUser

            every { authFileManager.readLinesFromFile() } returns lines
            every { userMapper.parseCsvLine(userCsv) } returns user

            // When
            val result = authRepository.isUserLoggedIn()

            // Then
            assertThat(result).isEqualTo(true)
        }
    }

}
