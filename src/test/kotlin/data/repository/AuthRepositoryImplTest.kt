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
import utils.UserMock
import java.io.FileNotFoundException
import java.io.IOException

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
    fun `saveUserLogin should return success when user is saved successfully`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } just runs

        val result = authRepository.saveUserLogin(user)

        assertThat(result.isSuccess).isTrue()
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

    @Test
    fun `saveUserLogin should return failure when an exception occurs`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"
        val exception = IOException("Failed to write file")

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } throws exception

        val result = authRepository.saveUserLogin(user)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `saveUserLogin should return exception when an exception occurs`() {
        val user = UserMock.adminUser
        val userCsv = "user-csv-string"
        val exception = IOException("Failed to write file")

        every { userMapper.serialize(user) } returns userCsv
        every { fileManager.writeLinesToFile(userCsv) } throws exception

        val result = authRepository.saveUserLogin(user)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `isUserLoggedIn should return success with true when file has content`() {
        val userCsv = "user-csv-string"
        val lines = listOf(userCsv)
        val user = UserMock.adminUser

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(userCsv) } returns user

        val result = authRepository.isUserLoggedIn()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
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
    fun `isUserLoggedIn should return success with false when file has only blank lines`() {
        val lines = listOf("", "  ", "\n")

        every { fileManager.readLinesFromFile() } returns lines

        val result = authRepository.isUserLoggedIn()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file has only blank lines`() {
        val lines = listOf("", "  ", "\n")

        every { fileManager.readLinesFromFile() } returns lines

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should return success with false when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        val result = authRepository.isUserLoggedIn()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when file not found`() {
        every { fileManager.readLinesFromFile() } throws FileNotFoundException()

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `isUserLoggedIn should return failure when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authRepository.isUserLoggedIn()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `isUserLoggedIn should return exception when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authRepository.isUserLoggedIn()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `isUserLoggedIn should not set user in session manager when other exception occurs`() {
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        authRepository.isUserLoggedIn()

        assertThat(SessionManger.isLoggedIn()).isFalse()
    }

    @Test
    fun `clearLogin should return success with true when file is cleared successfully`() {
        SessionManger.login(UserMock.adminUser)
        every { fileManager.clearFile() } just runs

        val result = authRepository.clearLogin()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
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

        val result = authRepository.clearLogin()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `clearLogin should return exception when an exception occurs`() {
        SessionManger.login(UserMock.adminUser)
        val exception = FileNotFoundException()

        every { fileManager.clearFile() } throws exception

        val result = authRepository.clearLogin()

        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `loginUser should return success when username and password are correct`() {
        val username = "validUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        val result = authRepository.loginUser(username, password)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `loginUser should return success message when username and password are correct`() {
        val username = "validUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        val result = authRepository.loginUser(username, password)

        assertThat(result.getOrNull()).isEqualTo("Login successfully")
    }

    @Test
    fun `loginUser should save user login when username and password are correct`() {
        val username = "validUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        authRepository.loginUser(username, password)

        assertThat(SessionManger.isLoggedIn()).isTrue()
    }

    @Test
    fun `loginUser should return failure when username is not found`() {
        val username = "invalidUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        val result = authRepository.loginUser(username, password)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `loginUser should return AuthenticationException when username is not found`() {
        val username = "invalidUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        val result = authRepository.loginUser(username, password)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.AuthenticationException::class.java)
    }

    @Test
    fun `loginUser should return failure when password is incorrect`() {
        val username = "validUser"
        val password = "wrongPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        val result = authRepository.loginUser(username, password)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `loginUser should return AuthenticationException when password is incorrect`() {
        val username = "validUser"
        val password = "wrongPass"
        val user = UserMock.validUser
        val lines = listOf("user-csv-string")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(any()) } returns user

        val result = authRepository.loginUser(username, password)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.AuthenticationException::class.java)
    }

    @Test
    fun `loginUser should return failure when file operation fails`() {
        val username = "validUser"
        val password = "validPass"
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authRepository.loginUser(username, password)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `loginUser should return IOException when file operation fails`() {
        val username = "validUser"
        val password = "validPass"
        val exception = IOException("Failed to read file")

        every { fileManager.readLinesFromFile() } throws exception

        val result = authRepository.loginUser(username, password)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `loginUser should filter out blank lines`() {
        val username = "validUser"
        val password = "validPass"
        val user = UserMock.validUser
        val lines = listOf("", "  ", "user-csv-string", "\n")

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine("user-csv-string") } returns user
        every { userMapper.serialize(user) } returns "user-csv-string"
        every { fileManager.writeLinesToFile(any()) } just runs

        val result = authRepository.loginUser(username, password)

        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `loginUser should throw EiffelFlowException directly when it occurs`() {
        val username = "validUser"
        val password = "validPass"
        val eiffelException = EiffelFlowException.AuthorizationException("Authorization failed")

        every { fileManager.readLinesFromFile() } throws eiffelException

        val result = authRepository.loginUser(username, password)

        assertThat(result.exceptionOrNull()).isEqualTo(eiffelException)
    }

    @Test
    fun `isUserLoggedIn should use first non-blank line when mixed with blank lines`() {
        val userCsv = "user-csv-string"
        val lines = listOf("", "  ", userCsv, "another-line")
        val user = UserMock.adminUser

        every { fileManager.readLinesFromFile() } returns lines
        every { userMapper.parseCsvLine(userCsv) } returns user

        val result = authRepository.isUserLoggedIn()

        assertThat(result.getOrNull()).isTrue()
    }

}
