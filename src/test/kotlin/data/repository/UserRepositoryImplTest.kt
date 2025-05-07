package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
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
import org.junit.jupiter.api.assertThrows
import utils.UserMock.adminUser
import utils.UserMock.existingUser
import utils.UserMock.fileNotFoundException
import utils.UserMock.multipleUsers
import utils.UserMock.newUserCsv
import utils.UserMock.oldUserCsv
import utils.UserMock.updateUser
import utils.UserMock.userById
import utils.UserMock.userCsv
import utils.UserMock.userToDelete
import utils.UserMock.validUser
import java.util.*

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

    //region createUser
    @Test
    fun `createUser should return the created user on success`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { userMapper.serialize(validUser) } returns userCsv
            every { csvManager.readLinesFromFile() } returns emptyList()
            every { csvManager.writeLinesToFile(userCsv) } returns Unit
            coEvery { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()

            // When
            val result = userRepository.createUser(user = validUser)

            // Then
            assertThat(result).isEqualTo(validUser)
        }
    }
    @Test
    fun `createUser should throw Exception when file operation fails`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { userMapper.serialize(validUser) } returns userCsv
            every { csvManager.readLinesFromFile() } returns emptyList()
            every { csvManager.writeLinesToFile(userCsv) } throws fileNotFoundException

            // When/Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.createUser(user = validUser)
            }
            assertThat(exception.message).contains("Can't Create User")
        }
    }

    @Test
    fun `createUser should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { userMapper.serialize(validUser) } returns userCsv
            every { csvManager.readLinesFromFile() } returns emptyList()
            every { csvManager.writeLinesToFile(userCsv) } just Runs
            coEvery { auditRepository.createAuditLog(any()) } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.createUser(user = validUser)
            }
            assertThat(exception.message).contains("Can't Create User")
        }
    }

    @Test
    fun `createUser should throw Exception when username is already taken`() {
        runTest{
        // Given
        every { SessionManger.isAdmin() } returns true
        every { csvManager.readLinesFromFile() } returns listOf("user1")

        val existingUserWithSameUsername = User(
            userId = UUID.randomUUID(),
            username = validUser.username,
            password = "password",
            role = RoleType.MATE
        )

        every { userMapper.parseCsvLine("user1") } returns existingUserWithSameUsername

        // When & Then
        val exception = assertThrows<EiffelFlowException.AuthorizationException> {
            userRepository.createUser(validUser)
        }
        assertThat(exception.message).contains("Username '${validUser.username}' is already taken")
    }
    }

    @Test
    fun `createUser should throw Exception when user is not admin`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns false
            every { csvManager.readLinesFromFile() } returns emptyList()

            // When/Then
            val exception = assertThrows<EiffelFlowException.AuthorizationException> {
                userRepository.createUser(user = validUser)
            }
            assertThat(exception.message).contains("Only admin can create or update user")
        }
    }
    //endregion



    //region updateUser
    @Test
    fun `updateUser should return the updated user on success`() {
        runTest {
            // Given
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns existingUser
            every { userMapper.serialize(existingUser) } returns oldUserCsv
            every { userMapper.serialize(updateUser) } returns newUserCsv
            every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
            coEvery { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()

            // When
            val result = userRepository.updateUser(updateUser)

            // Then
            assertThat(result).isEqualTo(updateUser)
        }
    }
    @Test
    fun `updateUser should throw Exception when user is not found`() {
        runTest {
            // Given
            val nonMatchingUser = validUser.copy(userId = UUID.randomUUID())
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns nonMatchingUser

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.updateUser(user = validUser)
            }
            assertThat(exception.message).contains("Can't Update User")
        }
    }

    @Test
    fun `updateUser should return Exception when file operation fails`() {
        runTest {
            // Given
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns existingUser
            every { userMapper.serialize(existingUser) } returns oldUserCsv
            every { userMapper.serialize(updateUser) } returns newUserCsv
            every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.updateUser(user = validUser)
            }
            assertThat(exception.message).contains("Can't Update User")
        }
    }

    @Test
    fun `updateUser should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns existingUser
            every { userMapper.serialize(existingUser) } returns oldUserCsv
            every { userMapper.serialize(updateUser) } returns newUserCsv
            every { csvManager.updateLinesToFile(newUserCsv, oldUserCsv) } returns Unit
            coEvery { auditRepository.createAuditLog(any()) } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.updateUser(user = validUser)
            }
            assertThat(exception.message).contains("Can't Update User")
        }
    }
    //endregion


    //region deleteUser
    @Test
    fun `deleteUser should return the deleted user on success`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns userToDelete
            every { userMapper.serialize(userToDelete) } returns userCsv
            every { csvManager.deleteLineFromFile(userCsv) } returns Unit
            coEvery { auditRepository.createAuditLog(any()) } returns mockk<AuditLog>()

            // When
            val result = userRepository.deleteUser(userToDelete.userId)

            // Then
            assertThat(result).isEqualTo(userToDelete)
        }
    }

    @Test
    fun `deleteUser should throw Exception when user is not admin`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns false

            // When & Then
            val exception = assertThrows<EiffelFlowException.AuthorizationException> {
                userRepository.deleteUser(UUID.randomUUID())
            }
            assertThat(exception.message).contains("Only admin can create or update user")
        }
    }
    @Test
    fun `deleteUser should throw Exception when user is not found`() {
        runTest{
        //Given
        every { SessionManger.isAdmin() } returns true
        every { SessionManger.getUser() } returns adminUser
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser

        // When & Then
        val exception = assertThrows<EiffelFlowException.IOException> {
            userRepository.deleteUser(UUID.randomUUID())
        }
        assertThat(exception.message).contains("Can't Delete User")
    }
}
    @Test
    fun `deleteUser should throw Exception when file operation fails`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns userToDelete
            every { userMapper.serialize(userToDelete) } returns userCsv
            every { csvManager.deleteLineFromFile(userCsv) } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.deleteUser(userToDelete.userId)
            }
            assertThat(exception.message).contains("Can't Delete User")
        }
    }

    @Test
    fun `deleteUser should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            every { SessionManger.isAdmin() } returns true
            every { SessionManger.getUser() } returns adminUser
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns userToDelete
            every { userMapper.serialize(userToDelete) } returns userCsv
            every { csvManager.deleteLineFromFile(userCsv) } returns Unit
            coEvery { auditRepository.createAuditLog(any()) } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.deleteUser(userToDelete.userId)
            }
            assertThat(exception.message).contains("Can't Delete User")
        }
    }
    //endregion


    //region getUserById
    @Test
    fun `getUserById should return the user on success`() {
        runTest {
            // Given
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine(any()) } returns userById

            // When
            val result = userRepository.getUserById(userById.userId)

            // Then
            assertThat(result).isEqualTo(userById)
        }
    }
    @Test
    fun `getUserById should throw Exception when user is not found`() {
        runTest{
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine(any()) } returns validUser

        // When & Then
        val exception = assertThrows<EiffelFlowException.IOException> {
            userRepository.getUserById(UUID.randomUUID())
        }
        assertThat(exception.message).contains("Can't get User")
    }
}
    @Test
    fun `getUserById should throw Exception when file operation fails`() {
        runTest {
            // Given
            every { csvManager.readLinesFromFile() } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.getUserById(userById.userId)
            }
            assertThat(exception.message).contains("Can't get User")
        }
    }
    @Test
    fun `getUserById should convert non-EiffelFlowException to IOException`() {
        runTest {
            // Given
            every { csvManager.readLinesFromFile() } throws customException

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.getUserById(UUID.randomUUID())
            }
            assertThat(exception.message).contains("Can't get User")
        }
    }
    @Test
    fun `getUserById should filter blank lines`() {
        runTest {
            // Given
            val randomId = UUID.randomUUID()
            every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
            every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
            every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]

            // When & Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.getUserById(randomId)
            }
            assertThat(exception.message).contains("User with ID $randomId not found")
        }
    }
    //endregion


    // region getUsers
    @Test
    fun `getUsers should return success when users are found`() {
        runTest {
            // Given
            every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
            every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]

            // When
            val result = userRepository.getUsers()

            // Then
            assertThat(result).isEqualTo(multipleUsers)
        }
    }
    @Test
    fun `getUsers should return list with correct size on success`() {
        runTest{
        // Given
        every { csvManager.readLinesFromFile() } returns listOf("line1", "line2")
        every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
        every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]

        // When
        val result = userRepository.getUsers()

        // Then
        assertThat(result.size).isEqualTo(2)
    }
}
    @Test
    fun `getUsers should throw exception when file is not found`() {
        runTest {
            // given
            every { csvManager.readLinesFromFile() } throws fileNotFoundException

            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.getUsers()
            }
            assertThat(exception.message).contains("Can't get Users because ${fileNotFoundException.message}")
        }
    }

    @Test
    fun `getUsers should throw Exception when other exception occurs`() {
        runTest {
            // Given
            every { csvManager.readLinesFromFile() } throws customException

            // When / Then
            val exception = assertThrows<EiffelFlowException.IOException> {
                userRepository.getUsers()
            }
            assertThat(exception.message).contains("Can't get Users ")
        }
    }
    @Test
    fun `getUsers should handle empty file correctly`() {
        runTest{
        every { csvManager.readLinesFromFile() } returns emptyList()

        val result = userRepository.getUsers()

        assertThat(result).isEmpty()
    }
}
    @Test
    fun `getUsers should filter blank lines`() {
        runTest {
            every { csvManager.readLinesFromFile() } returns listOf("line1", "", "line2", "   ")
            every { userMapper.parseCsvLine("line1") } returns multipleUsers[0]
            every { userMapper.parseCsvLine("line2") } returns multipleUsers[1]

            val result = userRepository.getUsers()

            assertThat(result.size).isEqualTo(2)
        }
    }

    @Test
    fun `getUsers should handle blank and non-blank lines`() {
        runTest {
            val fileLines = listOf("validLine", "", "  ", "\n", "\t", "anotherValidLine")
            every { csvManager.readLinesFromFile() } returns fileLines
            every { userMapper.parseCsvLine("validLine") } returns multipleUsers[0]
            every { userMapper.parseCsvLine("anotherValidLine") } returns multipleUsers[1]

            val result = userRepository.getUsers()

            assertThat(result.size).isEqualTo(2)
        }
    }
    //endregion

    companion object{
        val customException = EiffelFlowException.IOException("Custom exception")
    }
}


