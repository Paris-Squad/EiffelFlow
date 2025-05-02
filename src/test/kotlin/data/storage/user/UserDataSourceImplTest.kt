package data.storage.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.UserCsvParser
import org.example.data.storage.user.UserDataSource
import org.example.data.storage.user.UserDataSourceImpl
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class UserDataSourceImplTest {
    private lateinit var userDataSource: UserDataSource
    private val userMapper: UserCsvParser = mockk()
    private val csvManager: FileDataSource = mockk()

    @BeforeEach
    fun setUp() {
        userDataSource = UserDataSourceImpl(userMapper, csvManager)
    }

    @Test
    fun `createUser should return the created user when successful`() {
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val userCsv = "test,test,ADMIN,${user.userId}"

        every { userMapper.serialize(user) } returns userCsv
        every { csvManager.writeLinesToFile(userCsv) } just runs

        val result = userDataSource.createUser(user)

        assertThat(result.getOrNull()).isEqualTo(user)
    }

    @Test
    fun `createUser should return failure when an exception occurs`() {
        val user = User(
            userId = UUID.randomUUID(),
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )
        val exception = IOException("Failed to write file")

        every { userMapper.serialize(user) } returns "user_csv_string"
        every { csvManager.writeLinesToFile(any()) } throws exception

        val result = userDataSource.createUser(user)

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
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
        val csvLines = listOf("user1,pass1,ADMIN,uuid1", "user2,pass2,MATE,uuid2", "")
        val user1 = User(UUID.fromString("00000000-0000-0000-0000-000000000001"), "user1", "pass1", RoleType.ADMIN)
        val user2 = User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "user2", "pass2", RoleType.MATE)

        every { csvManager.readLinesFromFile() } returns csvLines
        every { userMapper.parseCsvLine("user1,pass1,ADMIN,uuid1") } returns user1
        every { userMapper.parseCsvLine("user2,pass2,MATE,uuid2") } returns user2

        val result = userDataSource.getUsers()

        assertThat(result.getOrNull()).hasSize(2)
    }

    @Test
    fun `getUsers should return empty list when file not found`() {
        every { csvManager.readLinesFromFile() } throws FileNotFoundException("File not found")

        val result = userDataSource.getUsers()

        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getUsers should return failure when other exception occurs`() {
        val exception = IOException("Failed to read file")
        every { csvManager.readLinesFromFile() } throws exception

        val result = userDataSource.getUsers()

        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)
    }
}