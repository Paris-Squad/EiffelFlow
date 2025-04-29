package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.data.repository.UserRepositoryImpl
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.user.UserDataSource
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

// todo change all of the test
class UserRepositoryImplTest {
    private lateinit var userRepository: UserRepositoryImpl
    private val userDataSource: UserDataSource = mockk()
    private val auditDataSource: AuditDataSource = mockk()

    @BeforeEach
    fun setUp() {
        userRepository = UserRepositoryImpl(userDataSource, auditDataSource)
    }

    @Test
    fun `createUser should return the created user`() {
        val user = User(
            username = "test",
            password = "test",
            role = RoleType.ADMIN
        )

        try {
            userRepository.createUser(user)
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
            userRepository.updateUser(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteUser should return the deleted user`() {
        val userId = UUID.randomUUID()

        try {
            userRepository.deleteUser(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUser should return user by ID`() {
        val userId = UUID.randomUUID()

        try {
            userRepository.getUserById(userId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getUsers should return list of users`() {
        try {
            userRepository.getUsers()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}