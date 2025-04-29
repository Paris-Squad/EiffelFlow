package data.storge.user

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.data.storge.CsvStorageManager
import org.example.data.storge.mapper.UserCsvMapper
import org.example.data.storge.user.UserDataSource
import org.example.data.storge.user.UserDataSourceImpl
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun `createUser should return the created user`() {
        val user = User(
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )

        try {
            userDataSource.createUser(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
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
    fun `getUsers should return list of users`() {
        try {
            userDataSource.getUsers()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}