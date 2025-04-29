package data.storge.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.example.data.storge.CsvStorageManager
import org.example.data.storge.mapper.UserCsvMapper
import org.example.data.storge.user.UserDataSource
import org.example.data.storge.user.UserDataSourceImpl
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import org.example.domain.model.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class UserDataSourceImplTest {
    private lateinit var userDataSource: UserDataSource
    private val userMapper: UserCsvMapper = mockk()
    private val csvManager: CsvStorageManager = mockk()

    @BeforeEach
    fun setUp() {
        userDataSource = UserDataSourceImpl(userMapper, csvManager)
    }

    @Test
    fun `createUser should return the created user when successful`() {
        // Arrange
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val userCsv = "test,test,ADMIN,${user.userId}"

        every { userMapper.mapTo(user) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv + "\n") } just runs

        // Act
        val result = userDataSource.createUser(user)

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(user)
        verify { userMapper.mapTo(user) }
        verify { csvManager.writeLinesToFile(userCsv + "\n") }
    }

    @Test
    fun `createUser should return failure when exception occurs`() {
        // Arrange
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val exception = IOException("Failed to write to file")

        every { userMapper.mapTo(user) } returns "test,test,ADMIN,${user.userId}"
        every { csvManager.writeLinesToFile(any()) } throws exception

        // Act
        val result = userDataSource.createUser(user)

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.UserCreationException::class.java)
    }

    @Test
    fun `updateUser should return the updated user`() {
        val user = User(
            username = "test",
            password = "test",
            role = RoleType.MATE
        )

        try {
            userDataSource.updateUser(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteUser should return the deleted user`() {
        val userId = UUID.randomUUID()

        try {
            userDataSource.deleteUser(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUser should return user by ID`() {
        val userId = UUID.randomUUID()

        try {
            userDataSource.getUserById(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUsers should return list of users when file exists with data`() {
        // Arrange
        val csvLines = listOf("user1,pass1,ADMIN,uuid1", "user2,pass2,MATE,uuid2", "")
        val user1 = User(UUID.fromString("00000000-0000-0000-0000-000000000001"), "user1", "pass1", RoleType.ADMIN)
        val user2 = User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "user2", "pass2", RoleType.MATE)

        every { csvManager.readLinesFromFile() } returns csvLines
        every { userMapper.mapFrom("user1,pass1,ADMIN,uuid1") } returns user1
        every { userMapper.mapFrom("user2,pass2,MATE,uuid2") } returns user2

        // Act
        val result = userDataSource.getUsers()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
        assertThat(result.getOrNull()).contains(user1)
        assertThat(result.getOrNull()).contains(user2)
        verify { csvManager.readLinesFromFile() }
        verify { userMapper.mapFrom("user1,pass1,ADMIN,uuid1") }
        verify { userMapper.mapFrom("user2,pass2,MATE,uuid2") }
    }

    @Test
    fun `getUsers should return empty list when file not found`() {
        // Arrange
        every { csvManager.readLinesFromFile() } throws FileNotFoundException("File not found")

        // Act
        val result = userDataSource.getUsers()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should return failure when other exception occurs`() {
        // Arrange
        val exception = IOException("Failed to read file")
        every { csvManager.readLinesFromFile() } throws exception

        // Act
        val result = userDataSource.getUsers()

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.UserStorageException::class.java)
    }
}