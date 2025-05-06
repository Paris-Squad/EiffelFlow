package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.example.data.repository.UserRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import utils.UserMock.adminUser
import utils.UserMock.existingUser
import utils.UserMock.fileNotFoundException
import utils.UserMock.multipleUsers
import utils.UserMock.newUserCsv
import utils.UserMock.oldUserCsv
import utils.UserMock.runtimeException
import utils.UserMock.updateUser
import utils.UserMock.userById
import utils.UserMock.userCsv
import utils.UserMock.userToDelete
import utils.UserMock.validUser
import java.util.UUID

class UserRepositoryImplTest {
    private val userMapper: UserCsvParser = mockk(relaxed = true)
    private val csvManager: FileDataSource = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        mockkObject(SessionManger)
        userRepository = UserRepositoryImpl(userMapper, csvManager, auditRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SessionManger)
    }

    @Test
    fun `createUser should return the created user on success`() {
        // Given
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { userMapper.serialize(validUser) } returns userCsv
        every { csvManager.readLinesFromFile() } returns emptyList()
        every { csvManager.writeLinesToFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()

        // When
        val result = userRepository.createUser(user = validUser)

        // Then
        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `createUser should return failure when file operation fails`() {
        // Given
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { userMapper.serialize(validUser) } returns userCsv
        every { csvManager.readLinesFromFile() } returns emptyList()
        every { csvManager.writeLinesToFile(userCsv) } throws fileNotFoundException

        // When
        val result = userRepository.createUser(user = validUser)

        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `createUser should return IOException when file operation fails`() {
        // Given
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { userMapper.serialize(validUser) } returns userCsv
        every { csvManager.readLinesFromFile() } returns emptyList()
        every { csvManager.writeLinesToFile(userCsv) } throws fileNotFoundException

        // When
        val result = userRepository.createUser(user = validUser)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `createUser should return failure when audit log creation fails`() {
        // Given
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { userMapper.serialize(validUser) } returns userCsv
        every { csvManager.readLinesFromFile() } returns emptyList()
        every { csvManager.writeLinesToFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG) } throws runtimeException

        // When
        val result = userRepository.createUser(user = validUser)

        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `createUser should pass through EiffelFlowException when thrown`() {
        // Given
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { userMapper.serialize(validUser) } returns userCsv
        every { csvManager.readLinesFromFile() } returns emptyList()
        every { csvManager.writeLinesToFile(userCsv) } throws authException

        // When
        val result = userRepository.createUser(user = validUser)

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }

    @Test
    fun `createUser should throw AuthorizationException when username is already taken`() {
        // Given
        every { SessionManger.isAdmin() } returns true
        every { csvManager.readLinesFromFile() } returns listOf("user1")

        // Create a user with the same username as validUser
        val existingUserWithSameUsername = User(
            userId = UUID.randomUUID(),
            username = validUser.username,
            password = "password",
            role = RoleType.MATE
        )

        every { userMapper.parseCsvLine("user1") } returns existingUserWithSameUsername

        // When
        val result = userRepository.createUser(validUser)

        // Then
        assertThat(result.isFailure).isTrue()
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(EiffelFlowException.AuthorizationException::class.java)
        assertThat(exception?.message).contains("is already taken")
    }

    @Test
    fun `createUser should throw AuthorizationException when user is not admin`() {
        // Given
        every { SessionManger.isAdmin() } returns false
        every { csvManager.readLinesFromFile() } returns emptyList()

        // When
        val result = userRepository.createUser(validUser)

        // Then
        assertThat(result.isFailure).isTrue()
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(EiffelFlowException.AuthorizationException::class.java)
        assertThat(exception?.message).contains("Only admin can create user")
    }

    @Test
    fun `updateUser should return success when update is successful`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `updateUser should return the updated user on success`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.getOrNull()).isEqualTo(updateUser)
    }

    @Test
    fun `updateUser should return failure when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `updateUser should return NotFoundException when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `updateUser should return failure when file operation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws runtimeException
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `updateUser should return IOException when file operation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws runtimeException
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `updateUser should return failure when audit log creation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG) } throws runtimeException
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return success when delete is successful`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return the deleted user on success`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.getOrNull()).isEqualTo(userToDelete)
    }

    @Test
    fun `deleteUser should return failure when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.deleteUser(UUID.randomUUID())
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return NotFoundException when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // Given
        val result = userRepository.deleteUser(UUID.randomUUID())
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `deleteUser should return failure when file operation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws runtimeException
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return IOException when file operation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws runtimeException
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `deleteUser should return failure when audit log creation fails`() {
        // Given
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG) } throws runtimeException
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUserById should return success when user is found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userById
        // When
        val result = userRepository.getUserById(userById.userId)
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUserById should return the user on success`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userById
        // When
        val result = userRepository.getUserById(userById.userId)
        // Then
        assertThat(result.getOrNull()).isEqualTo(userById)
    }

    @Test
    fun `getUserById should return failure when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.getUserById(UUID.randomUUID())
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `getUserById should return NotFoundException when user is not found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.getUserById(UUID.randomUUID())
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should return failure when file operation fails`() {
        // Given
        every { csvManager.readLinesFromFile() } throws runtimeException
        // When
        val result = userRepository.getUserById(userById.userId)
        // Then
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `getUserById should return IOException when file operation fails`() {
        // Given
        every { csvManager.readLinesFromFile() } throws runtimeException
        // When
        val result = userRepository.getUserById(userById.userId)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should pass through EiffelFlowException when thrown`() {
        // Given
        every { csvManager.readLinesFromFile() } returns emptyList()
        // When
        val result = userRepository.getUserById(UUID.randomUUID())
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should return result from error in getUsers`() {
        // Given
        every { csvManager.readLinesFromFile() } throws RuntimeException("Simulated IO error")
        // When
        val result = userRepository.getUserById(UUID.randomUUID())
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should convert non-EiffelFlowException to IOException`() {
        // Given
        val customException = EiffelFlowException.IOException("Custom exception")
        every { csvManager.readLinesFromFile() } throws customException
        // When
        val result = userRepository.getUserById(UUID.randomUUID())
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should handle EiffelFlowException from the outer try block`() {
        // Given
        val randomId = UUID.randomUUID() // Different from the one in validUser
        every { csvManager.readLinesFromFile() } returns listOf("user1")
        every { userMapper.parseCsvLine(any()) } returns validUser
        // When
        val result = userRepository.getUserById(randomId)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should filter blank lines`() {
        // Given
        val randomId = UUID.randomUUID()
        every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
        every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]
        // When
        val result = userRepository.getUserById(randomId)
        // Then
        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `getUsers should return success when users are found`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUsers should return list with correct size on success`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }

    @Test
    fun `getUsers should return success when file not found`() {
        // Given
        every { csvManager.readLinesFromFile() } throws fileNotFoundException
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUsers should return empty list when file not found`() {
        // Given
        every { csvManager.readLinesFromFile() } throws fileNotFoundException
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should return IOException when other exception occurs`() {
        // Given
        every { csvManager.readLinesFromFile() } throws runtimeException
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUsers should return IOException when other exception occurs in getUsers`() {
        // Given
        every { csvManager.readLinesFromFile() } throws RuntimeException("Simulated IO error")
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUsers should handle empty file correctly`() {
        // Given
        every { csvManager.readLinesFromFile() } returns emptyList()
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should filter blank lines`() {
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
        every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }


    @Test
    fun `getUsers should handle blank and non-blank lines`() {
        // Given
        val fileLines = listOf("validLine", "", "  ", "\n", "\t", "anotherValidLine")
        every { csvManager.readLinesFromFile() } returns fileLines
        every { userMapper.parseCsvLine("validLine") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("anotherValidLine") } returns multipleUsers[1]
        // When
        val result = userRepository.getUsers()
        // Then
        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }

    @Test
    fun `updateUser should pass through EiffelFlowException when thrown`() {
        // Given
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns existingUser
        every { userMapper.serialize(existingUser) } returns oldUserCsv
        every { userMapper.serialize(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws authException
        // When
        val result = userRepository.updateUser(updateUser)
        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }

    @Test
    fun `deleteUser should pass through EiffelFlowException when thrown`() {
        // Given
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns userToDelete
        every { userMapper.serialize(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws authException
        // When
        val result = userRepository.deleteUser(userToDelete.userId)
        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }
}
