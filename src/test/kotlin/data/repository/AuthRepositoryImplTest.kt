package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
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
import kotlin.jvm.Throws
import kotlin.test.assertEquals

class AuthRepositoryImplTest {
    private lateinit var authRepository: AuthRepository
    private val fileManager: FileDataSource = mockk()
    private val userMapper: UserCsvParser = mockk()

    @BeforeEach
    fun setUp() {
        authRepository = AuthRepositoryImpl(fileManager, userMapper)
    }

    @AfterEach
    fun tearDown() {
        SessionManger.logout()
    }


    @Test
    fun `saveUserLogin should return User when user is saved successfully`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } just runs

        val result = authRepository.saveUserLogin(user)

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `saveUserLogin should store user in session manager`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } just runs

        authRepository.saveUserLogin(user)

        assertThat(SessionManger.getUser()).isEqualTo(user)
    }

    @Test
    fun `saveUserLogin should write to file`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } just runs

        authRepository.saveUserLogin(user)

        verify { fileManager.writeLinesToFile(userCsv) }
    }

    @Throws
    @Test
    fun `saveUserLogin should threw IOException when an exception occurs`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"
        val exception = IOException("Failed to write file")

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.saveUserLogin(user)
        }
    }

    @Test
    fun `saveUserLogin should return exception when an exception occurs`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"
        val exception = IOException("Failed to write file")

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.saveUserLogin(user)
        }
    }

    @Test
    fun `isUserLoggedIn should return true when file has content`() {
        val userCsv = "user-csv-string"
        val lines = listOf(userCsv)
        val user = UserMock.adminUser

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(userCsv) } returns user

        val result = authRepository.isUserLoggedIn()

        assertThat(result).isTrue()
    }

    @Test
    fun `isUserLoggedIn should set user in session manager when file has content`() {
        val userCsv = "user-csv-string"
        val lines = listOf(userCsv)
        val user = UserMock.adminUser

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(userCsv) } returns user

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isTrue()
        assertThat(SessionManger.getUser()).isEqualTo(user)
    }

    @Test
    fun `isUserLoggedIn should return false when file has only blank lines`() {
        val lines = listOf("", "  ", "\n")

        every { fileManager.readLinesFromFile() } returns lines

        val result = authRepository.isUserLoggedIn()

        assertThat(result).isFalse()
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file has only blank lines`() {
        val lines = listOf("", "  ", "\n")

        every { fileManager.readLinesFromFile() } returns lines

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should return false when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        val result = authRepository.isUserLoggedIn()

        assertThat(result).isFalse()
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should threw IOException when other exception occurs`() {
        val exception = EiffelFlowException.IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.isUserLoggedIn()
        }
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.isUserLoggedIn()
        }
    }

    @Test
    fun `clearLogin should clear login data when file is cleared successfully`() {
        SessionManger.login(UserMock.adminUser)
        every { fileManager.clearFile() } just runs

        authRepository.clearLogin()

        assertThrows<EiffelFlowException.AuthorizationException> {
            SessionManger.getUser()
        }
    }

    @Test
    fun `clearLogin should logout user from session manager`() {
        SessionManger.login(UserMock.adminUser)
        every { fileManager.clearFile() } just runs

        authRepository.clearLogin()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `clearLogin should return failure when an exception occurs`() {
        SessionManger.login(UserMock.adminUser)
        val exception = FileNotFoundException()

        every { fileManager.clearFile() } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.clearLogin()
        }
    }

    @Test
    fun `loginUser should return success when username and password are correct`() {
        val username = "validUser"
        val password = "validPass"
        val user = validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        val result = authRepository.loginUser(username, password)

    //    assertEquals(result).equals(user)
        assertEquals(user, result)
    }

    @Test
    fun `loginUser should save user login when username and password are correct`() {
        val username = "validUser"
        val password = "validPass"
        val user = validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        authRepository.loginUser(username, password)

        assertThat(SessionManger.isLoggedIn()).isTrue()
    }

    @Test
    fun `loginUser should return AuthenticationException when username is not found`() {
        val username = "invalidUser"
        val password = "validPass"
        val user = validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        assertThrows<EiffelFlowException.AuthenticationException> {
            authRepository.loginUser(username, password)
        }
    }

    @Test
    fun `loginUser should return AuthenticationException when password is incorrect`() {
        val username = "validUser"
        val password = "wrongPass"
        val user = validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        assertThrows<EiffelFlowException.AuthenticationException> {
            authRepository.loginUser(username, password)
        }
    }

    @Test
    fun `loginUser should return IOException when file operation fails`() {
        val username = "validUser"
        val password = "validPass"
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        assertThrows<EiffelFlowException.IOException> {
            authRepository.loginUser(username, password)
        }
    }

    @Test
    fun `loginUser should filter out blank lines`() {
        val username = "validUser"
        val password = "validPass"
        val user = validUser
        val lines = listOf("", "  ", "user-csv-string", "\n")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine("user-csv-string") } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        val result = authRepository.loginUser(username, password)

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `loginUser should throw EiffelFlowException directly when it occurs`() {
        val username = "validUser"
        val password = "validPass"
        val eiffelException = EiffelFlowException.AuthorizationException("Authorization failed")

        every { fileManager.readLinesFromFile() } throws eiffelException

        assertThrows<EiffelFlowException.AuthorizationException> {
            authRepository.loginUser(username, password)
        }
    }

    @Test
    fun `isUserLoggedIn should use first non-blank line when mixed with blank lines`() {
        val userCsv = "user-csv-string"
        val lines = listOf("", "  ", userCsv, "another-line")
        val user = UserMock.adminUser

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(userCsv) } returns user

        val result = authRepository.isUserLoggedIn()

        assertThat(result).isEqualTo(true)
    }

}
