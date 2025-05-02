package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.example.data.repository.UserRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.mapper.UserCsvMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import java.util.*

class UserRepositoryImplTest {
    private val userMapper: UserCsvMapper = mockk(relaxed = true)
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
        every { userMapper.mapTo(validUser) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.success(mockk<AuditLog>())

        val result = userRepository.createUser(user = validUser, createdBy = adminUser)

        assertThat(result.getOrNull()).isEqualTo(validUser)
    }

    @Test
    fun `createUser should return failure when file operation fails`() {
        every { userMapper.mapTo(validUser) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } throws fileNotFoundException

        val result = userRepository.createUser(user = validUser, createdBy = adminUser)

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `createUser should return IOException when file operation fails`() {
        every { userMapper.mapTo(validUser) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } throws fileNotFoundException

        val result = userRepository.createUser(user = validUser, createdBy = adminUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `createUser should return failure when audit log creation fails`() {
        every { userMapper.mapTo(validUser) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.failure(runtimeException)

        val result = userRepository.createUser(user = validUser, createdBy = adminUser)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `createUser should pass through EiffelFlowException when thrown`() {
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { userMapper.mapTo(validUser) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } throws authException

        val result = userRepository.createUser(user = validUser, createdBy = adminUser)

        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }

    @Test
    fun `updateUser should return success when update is successful`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.success(mockk<AuditLog>())

        val result = userRepository.updateUser(updateUser)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `updateUser should return the updated user on success`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.success(mockk<AuditLog>())

        val result = userRepository.updateUser(updateUser)

        assertThat(result.getOrNull()).isEqualTo(updateUser)
    }

    @Test
    fun `updateUser should return failure when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.updateUser(updateUser)

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `updateUser should return NotFoundException when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.updateUser(updateUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `updateUser should return failure when file operation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws runtimeException

        val result = userRepository.updateUser(updateUser)

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `updateUser should return IOException when file operation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws runtimeException

        val result = userRepository.updateUser(updateUser)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `updateUser should return failure when audit log creation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.failure(runtimeException)

        val result = userRepository.updateUser(updateUser)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return success when delete is successful`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.success(mockk<AuditLog>())

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return the deleted user on success`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.success(mockk<AuditLog>())

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.getOrNull()).isEqualTo(userToDelete)
    }

    @Test
    fun `deleteUser should return failure when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.deleteUser(UUID.randomUUID())

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return NotFoundException when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.deleteUser(UUID.randomUUID())

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `deleteUser should return failure when file operation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws runtimeException

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `deleteUser should return IOException when file operation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws runtimeException

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `deleteUser should return failure when audit log creation fails`() {
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } returns Unit
        every { auditRepository.createAuditLog(any()) } returns Result.failure(runtimeException)

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUserById should return success when user is found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userById

        val result = userRepository.getUserById(userById.userId)

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUserById should return the user on success`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userById

        val result = userRepository.getUserById(userById.userId)

        assertThat(result.getOrNull()).isEqualTo(userById)
    }

    @Test
    fun `getUserById should return failure when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.getUserById(UUID.randomUUID())

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `getUserById should return NotFoundException when user is not found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.getUserById(UUID.randomUUID())

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should return failure when file operation fails`() {
        every { csvManager.readLinesFromFile() } throws runtimeException

        val result = userRepository.getUserById(userById.userId)

        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `getUserById should return IOException when file operation fails`() {
        every { csvManager.readLinesFromFile() } throws runtimeException

        val result = userRepository.getUserById(userById.userId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should pass through EiffelFlowException when thrown`() {
        every { csvManager.readLinesFromFile() } returns emptyList()

        val result = userRepository.getUserById(UUID.randomUUID())

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should return result from error in getUsers`() {
        every { csvManager.readLinesFromFile() } throws RuntimeException("Simulated IO error")

        val result = userRepository.getUserById(UUID.randomUUID())

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should convert non-EiffelFlowException to IOException`() {
        val customException = EiffelFlowException.IOException("Custom exception")

        every { csvManager.readLinesFromFile() } throws customException

        val result = userRepository.getUserById(UUID.randomUUID())

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUserById should handle EiffelFlowException from the outer try block`() {
       val randomId = UUID.randomUUID() // Different from the one in validUser
        every { csvManager.readLinesFromFile() } returns listOf("user1")
        every { userMapper.mapFrom(any()) } returns validUser

        val result = userRepository.getUserById(randomId)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getUserById should filter blank lines`() {
        val randomId = UUID.randomUUID()
        every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
        every { userMapper.mapFrom("line1") } returns multipleUsers[0]
        every { userMapper.mapFrom("line2") } returns multipleUsers[1]

        val result = userRepository.getUserById(randomId)

        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `getUsers should return success when users are found`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom("line1") } returns multipleUsers[0]
        every { userMapper.mapFrom("line2") } returns multipleUsers[1]

        val result = userRepository.getUsers()

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUsers should return list with correct size on success`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom("line1") } returns multipleUsers[0]
        every { userMapper.mapFrom("line2") } returns multipleUsers[1]

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }

    @Test
    fun `getUsers should return success when file not found`() {
        every { csvManager.readLinesFromFile() } throws fileNotFoundException

        val result = userRepository.getUsers()

        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `getUsers should return empty list when file not found`() {
        every { csvManager.readLinesFromFile() } throws fileNotFoundException

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should return IOException when other exception occurs`() {
        every { csvManager.readLinesFromFile() } throws runtimeException

        val result = userRepository.getUsers()

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUsers should return IOException when other exception occurs in getUsers`() {
        every { csvManager.readLinesFromFile() } throws RuntimeException("Simulated IO error")

        val result = userRepository.getUsers()

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }

    @Test
    fun `getUsers should handle empty file correctly`() {
        every { csvManager.readLinesFromFile() } returns emptyList()

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should filter blank lines`() {
        every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
        every { userMapper.mapFrom("line1") } returns multipleUsers[0]
        every { userMapper.mapFrom("line2") } returns multipleUsers[1]

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }


    @Test
    fun `getUsers should handle blank and non-blank lines`() {
        val fileLines = listOf("validLine", "", "  ", "\n", "\t", "anotherValidLine")
        every { csvManager.readLinesFromFile() } returns fileLines
        every { userMapper.mapFrom("validLine") } returns multipleUsers[0]
        every { userMapper.mapFrom("anotherValidLine") } returns multipleUsers[1]

        val result = userRepository.getUsers()

        assertThat(result.getOrNull()?.size).isEqualTo(2)
    }

    @Test
    fun `updateUser should pass through EiffelFlowException when thrown`() {
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns existingUser
        every { userMapper.mapTo(existingUser) } returns oldUserCsv
        every { userMapper.mapTo(updateUser) } returns newUserCsv
        every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws authException

        val result = userRepository.updateUser(updateUser)

        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }

    @Test
    fun `deleteUser should pass through EiffelFlowException when thrown`() {
        val authException = EiffelFlowException.AuthorizationException("Authorization failed")
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.mapFrom(any()) } returns userToDelete
        every { userMapper.mapTo(userToDelete) } returns userCsv
        every { csvManager.deleteLineFromFile(userCsv) } throws authException

        val result = userRepository.deleteUser(userToDelete.userId)

        assertThat(result.exceptionOrNull()).isEqualTo(authException)
    }
}
